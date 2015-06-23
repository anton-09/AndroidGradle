package ru.home.pw;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

public class DataModel
{
    public SparseArray<Char> mChars;
    public SparseArray<Event> mEvents;
    public SparseArray<CharEvent> mCharsEvents;
    public SparseArray<Storage> mStorages;
    private long mDate;
    private DailyDbAdapter mDailyDbAdapter;
    public static ArrayList<Integer> mListOfStatuses;
    static Context mContext;

    public class Char
    {
        public Integer mId;
        public String mName;
        public String mPicture;
        public Integer mLevel;
        
        public Char(Integer id, String name, String picture, Integer level)
        {
            mId = id;
            mName = name;
            mPicture = picture;
            mLevel = level;
        }

        public void LevelUp()
        {
            mDailyDbAdapter.updateLevelById(mId);
        }
    }
    
    public class Event
    {
        public Integer mId;
        public String mName;
        
        public Event(Integer id, String name)
        {
            mId = id;
            mName = name;
        }
    }
    
    public class CharEvent
    {
        public Integer mId;
        public Char mChar;
        public Event mEvent;
        public boolean mUsed;
        
        public CharEvent(Integer id, Char c, Event e, int used)
        {
            mId = id;
            mChar = c;
            mEvent = e;
            mUsed = (used == 1);
        }
    }

    public class Storage
    {
        public Integer mId;
        public CharEvent mCharEvent;
        public Date mDate;
        public int mStatus;
        public String mComment;

        public Storage(Integer id, CharEvent ce, long date, int status)
        {
            mId = id;
            mCharEvent = ce;
            mDate = new Date(date);
            mStatus = status;
            mComment = "";
        }

        public Storage(int status, String comment)
        {
            mId = -1;
            mCharEvent = null;
            mDate = null;
            mStatus = status;
            mComment = comment;
        }

        public void OffStatus()
        {
            mCharEvent.mUsed = false;
            mDailyDbAdapter.updateUsedMappingById(mCharEvent.mChar.mId, mCharEvent.mEvent.mName, 0);
        }

        public void OnStatus()
        {
            mCharEvent.mUsed = true;
            mDailyDbAdapter.updateUsedMappingById(mCharEvent.mChar.mId, mCharEvent.mEvent.mName, 1);
        }

        public boolean ResetStatus()
        {
            if (DataModel.status2Drawable(mStatus) == R.drawable.yellow ||
                DataModel.status2Drawable(mStatus) == R.drawable.green)
            {
                mStatus = DataModel.drawable2Status(R.drawable.red);
                mDailyDbAdapter.updateRecord(mDate.getTime(), mCharEvent.mChar.mName, mCharEvent.mEvent.mName, mStatus);
                return true;
            }

            return false;
        }
        
        public int UpdateStatus()
        {
            switch (DataModel.status2Drawable(mStatus))
            {
                case R.drawable.red:
                    mStatus = DataModel.drawable2Status(R.drawable.yellow);
                    break;
                case R.drawable.yellow:
                    mStatus = DataModel.drawable2Status(R.drawable.green);
                    break;
                default:
                    return 0;
            }

            mDailyDbAdapter.updateRecord(mDate.getTime(), mCharEvent.mChar.mName, mCharEvent.mEvent.mName, mStatus);
            return mStatus;
        }
    }

    public DataModel(DailyDbAdapter dailyDbAdapter, Context context)
    {
        if (mListOfStatuses == null)
        {
            mListOfStatuses = new ArrayList<Integer>();
            mListOfStatuses.add(R.drawable.red);
            mListOfStatuses.add(R.drawable.yellow);
            mListOfStatuses.add(R.drawable.green);
            mListOfStatuses.add(R.drawable.gray);
            mListOfStatuses.add(R.drawable.black);
            mListOfStatuses.add(R.drawable.dru);
            mListOfStatuses.add(R.drawable.prist);
            mListOfStatuses.add(R.drawable.tank);
            mListOfStatuses.add(R.drawable.myst);
            mListOfStatuses.add(R.drawable.var);
            mListOfStatuses.add(R.drawable.straj);
        }

        mContext = context;
        mDailyDbAdapter = dailyDbAdapter;
        mChars = new SparseArray<Char>();
        mEvents = new SparseArray<Event>();
        mCharsEvents = new SparseArray<CharEvent>();
        mStorages = new SparseArray<Storage>();
    }

//    public static int char2Status(String picture)
//    {
//        int resID = mContext.getResources().getIdentifier(picture, "drawable", mContext.getPackageName());
//        return mListOfStatuses.indexOf(resID);
//    }

    public static int status2Drawable(int status)
    {
        return mListOfStatuses.get(status);
    }

    public static int drawable2Status(int drawable)
    {
        return mListOfStatuses.indexOf(drawable);
    }

    public void setDate(long date)
    {
        mDate = date;
    }
    
    public Object getItem(int position)
    {
        // Converting consequence number into matrix view
        int row = position / (mChars.size() + 1); // Event
        int column = position % (mChars.size() + 1); // Char

        if (row == 0 && column == 0)
            return null; // Empty Cell
        if (row == 0)
            return mChars.get(column); // Char Header
        if (column == 0)
            return mEvents.get(row); // Event Header


        //Event eventToFind = mEvents.get(row);
        //Char charToFind = mChars.get(column);
        
        Storage storageValue;
        // Try to find already existing item in Storage ...
        for(int i = 0; i < mStorages.size(); i++)
        {
            storageValue = mStorages.valueAt(i);
            if (    storageValue.mCharEvent.mChar.mId.equals(column) &&
                    storageValue.mCharEvent.mEvent.mId.equals(row))
                return storageValue; // Existing Storage Item
        }

        // ... If there is no existing item, find CharEvent object ...
        CharEvent charsEventsValue = null;
        for(int i = 0; i < mCharsEvents.size(); i++)
        {
            charsEventsValue = mCharsEvents.valueAt(i);
            if (    charsEventsValue.mChar.mId.equals(column) &&
                    charsEventsValue.mEvent.mId.equals(row))
            {
                break;
            }
        }

        // ... Here if CharEvent object being found is used
        // then we should insert new row into Storage Table and put new object into Storage HashMap ...
        assert charsEventsValue != null;
        if (charsEventsValue.mUsed) {
            mDailyDbAdapter.addRecord(charsEventsValue.mId, DataModel.drawable2Status(R.drawable.red), mDate);
            fillStoragesFromCursor();

            // Another try to find already existing item in Storage ...
            // It should be 100% success
            for(int i = 0; i < mStorages.size(); i++)
            {
                storageValue = mStorages.valueAt(i);
                if (    storageValue.mCharEvent.mChar.mId.equals(column) &&
                        storageValue.mCharEvent.mEvent.mId.equals(row))
                    return storageValue; // Existing Storage Item
            }
        }
        else
            return (new Storage(-1, charsEventsValue, mDate, DataModel.drawable2Status(R.drawable.gray))); // Fake Storage Item

        throw new UnsupportedOperationException("You can't read this sign, 'cause it's unreachable piece of shi... code");
    }
    
    public void fillCharsFromCursor()
    {
        Cursor cursor = mDailyDbAdapter.getChars();
        mChars.clear();
        Char c;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            c = new Char(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
            mChars.put(c.mId, c);
            cursor.moveToNext();
        }
        cursor.close();
    }
    
    public void fillEventsFromCursor()
    {
        Cursor cursor = mDailyDbAdapter.getEvents();
        mEvents.clear();
        Event event_;
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            event_ = new Event(cursor.getInt(0), cursor.getString(1));
            mEvents.put(event_.mId, event_);
            cursor.moveToNext();
        }
        cursor.close();
    }
    
    public void fillCharsEventsFromCursor()
    {
        Cursor cursor = mDailyDbAdapter.getCharsEvents();
        mCharsEvents.clear();
        CharEvent charEvent_;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            charEvent_ = new CharEvent(cursor.getInt(0), mChars.get(cursor.getInt(1)), mEvents.get(cursor.getInt(2)), cursor.getInt(3));
            mCharsEvents.put(charEvent_.mId, charEvent_);
            cursor.moveToNext();
        }
        cursor.close();
    }
    
    public void fillStoragesFromCursor()
    {
        Cursor cursor = mDailyDbAdapter.getStorage(mDate);
        mStorages.clear();
        Storage storage_;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            storage_ = new Storage(cursor.getInt(0), mCharsEvents.get(cursor.getInt(1)), cursor.getLong(2), cursor.getInt(3));
            if (storage_.mCharEvent.mUsed)
                mStorages.put(storage_.mId, storage_);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
