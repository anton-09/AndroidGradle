package ru.home.babylog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    SimpleDateFormat backupDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat backupHumanReadableDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    Context context;
    private List<String> records;

    public RecyclerViewAdapter(List<String> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        String record = records.get(i);

        try
        {
            viewHolder.name.setText(backupHumanReadableDateFormat.format(backupDateFormat.parse(record)));
        } catch (ParseException e)
        {
            viewHolder.name.setText(record);
        }
        viewHolder.deleteButtonListener.setRecord(record);
        viewHolder.restoreButtonListener.setRecord(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private void restore(final String record)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(context.getString(R.string.restore_backup) + record + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            FileReader fileReader = new FileReader(new File(new File(android.os.Environment.getExternalStorageDirectory(), "/BabyLog"), record));
                            BufferedReader bufferedReader = new BufferedReader(fileReader);

                            MyApplication.getDBAdapter().deleteAllData();

                            String line = "";
                            String[] item;
                            while ((line = bufferedReader.readLine()) != null) {
                                item = line.split(";", -1);
                                MyApplication.getDBAdapter().addData(item[0], Integer.parseInt(item[1]), item[2], item[3], item[4]);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                })
                .show();

    }

    private void delete(final String record)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(context.getString(R.string.delete_backup) + record + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int position = records.indexOf(record);
                        records.remove(position);
                        new File(new File(android.os.Environment.getExternalStorageDirectory(), "/BabyLog"), record).delete();
                        notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                })
                .show();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView name;
        private ImageButton restoreButton;
        private ImageButton deleteButton;
        private RestoreButtonListener restoreButtonListener;
        private DeleteButtonListener deleteButtonListener;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.listItemBackupDate);
            restoreButton = (ImageButton) itemView.findViewById(R.id.buttonRestoreBackup);
            deleteButton = (ImageButton) itemView.findViewById(R.id.buttonDeleteBackup);
            restoreButtonListener = new RestoreButtonListener();
            restoreButton.setOnClickListener(restoreButtonListener);
            deleteButtonListener = new DeleteButtonListener();
            deleteButton.setOnClickListener(deleteButtonListener);
        }
    }

    private class RestoreButtonListener implements View.OnClickListener
    {
        private String record;

        @Override
        public void onClick(View v) {
            restore(record);
        }

        public void setRecord(String record)
        {
            this.record = record;
        }
    }

    private class DeleteButtonListener implements View.OnClickListener
    {
        private String record;

        @Override
        public void onClick(View v) {
            delete(record);
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}
