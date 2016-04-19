package eu.thedarken.diagnosis;

import java.util.ArrayList;

import android.view.WindowManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Display;

public class Styles {
    private static SharedPreferences.Editor prefEditor;
    private SharedPreferences settings;
    private Context mContext;
    private Display display;

    public Styles(Context c) {
        mContext = c;
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefEditor = settings.edit();
        display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public void initLines() {
        if (!settings.getBoolean("initdone", false)) {
            String defl1 = "kmonaaafhdhcaabdgkgbhggbcohfhegjgmcoebhchcgbhjemgjhdhehiibncbnjjmhgbjnadaaabejaaaehdgjhkgfhihaaaaaaaaghhaeaaaaaaamhdhcaabbgkgbhggbcogmgbgoghcoejgohegfghgfhcbcockakephibihdiacaaabejaaafhggbgmhfgfhihcaabagkgbhggbcogmgbgoghcoeohfgngcgfhcigkmjfbnaljeoailacaaaahihaaaaaaaaehdhbaahoaaacaaaaaabdhdhbaahoaaacaaaaaablhdhbaahoaaacaaaaaaakhdhbaahoaaacaaaaaabahdhbaahoaaacaaaaaabbhi";
            String defl2 = "kmonaaafhdhcaabdgkgbhggbcohfhegjgmcoebhchcgbhjemgjhdhehiibncbnjjmhgbjnadaaabejaaaehdgjhkgfhihaaaaaaaabhhaeaaaaaaamhdhcaabbgkgbhggbcogmgbgoghcoejgohegfghgfhcbcockakephibihdiacaaabejaaafhggbgmhfgfhihcaabagkgbhggbcogmgbgoghcoeohfgngcgfhcigkmjfbnaljeoailacaaaahihaaaaaaaadhi";
            String defl3 = "";
            String defl4 = "";
            prefEditor.putString("layout.line0", defl1);
            prefEditor.putString("layout.line1", defl2);
            prefEditor.putString("layout.line2", defl3);
            prefEditor.putString("layout.line3", defl4);

            int line = 0;
            prefEditor.putBoolean("overlay.align.right.line" + line, false);
            prefEditor.putString("overlay.font.type.line" + line, "1");
            prefEditor.putString("overlay.x_pos.line" + line, "1");
            prefEditor.putString("overlay.y_pos.line" + line, "45");
            prefEditor.putInt("overlay.font.size.line" + line, 15);
            prefEditor.putInt("overlay.color.normal.line" + line, 0xff06ff00);
            prefEditor.putInt("overlay.color.alert.line" + line, 0xffffff00);
            prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
            prefEditor.putInt("overlay.color.background.line" + line, 0x70000000);

            line = 1;
            prefEditor.putBoolean("overlay.align.right.line" + line, false);
            prefEditor.putString("overlay.font.type.line" + line, "1");
            prefEditor.putString("overlay.x_pos.line" + line, "1");
            prefEditor.putString("overlay.y_pos.line" + line, "60");
            prefEditor.putInt("overlay.font.size.line" + line, 15);
            prefEditor.putInt("overlay.color.normal.line" + line, 0xff06ff00);
            prefEditor.putInt("overlay.color.alert.line" + line, 0xffffff00);
            prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
            prefEditor.putInt("overlay.color.background.line" + line, 0x70000000);

            line = 2;
            prefEditor.putBoolean("overlay.align.right.line" + line, false);
            prefEditor.putString("overlay.font.type.line" + line, "1");
            prefEditor.putString("overlay.x_pos.line" + line, "1");
            prefEditor.putString("overlay.y_pos.line" + line, "75");
            prefEditor.putInt("overlay.font.size.line" + line, 15);
            prefEditor.putInt("overlay.color.normal.line" + line, 0xff06ff00);
            prefEditor.putInt("overlay.color.alert.line" + line, 0xffffff00);
            prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
            prefEditor.putInt("overlay.color.background.line" + line, 0x70000000);

            line = 3;
            prefEditor.putBoolean("overlay.align.right.line" + line, false);
            prefEditor.putString("overlay.font.type.line" + line, "1");
            prefEditor.putString("overlay.x_pos.line" + line, "1");
            prefEditor.putString("overlay.y_pos.line" + line, "90");
            prefEditor.putInt("overlay.font.size.line" + line, 15);
            prefEditor.putInt("overlay.color.normal.line" + line, 0xff06ff00);
            prefEditor.putInt("overlay.color.alert.line" + line, 0xffffff00);
            prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
            prefEditor.putInt("overlay.color.background.line" + line, 0x70000000);

            prefEditor.putString("overlay.divider", "|");
            prefEditor.putBoolean("initdone", true);
            prefEditor.commit();
        }
    }

    public void setStyle1() {

        int lineno;

        ArrayList<Integer> line = new ArrayList<Integer>();
        line.add(4);
        line.add(19);
        line.add(10);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(16);
        line.add(17);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(25);
        line.add(26);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "60");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(2);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "60");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", "|");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle2() {

        int lineno;

        ArrayList<Integer> line = new ArrayList<Integer>();
        line.add(24);
        line.add(23);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "12");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(20);
        line.add(22);
        line.add(21);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "12");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(2);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", "~");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle3() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        line.add(4);
        line.add(10);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "2");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(2);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " # ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle4() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        line.add(5);
        line.add(6);
        line.add(8);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(11);
        line.add(19);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "45");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " /\\ ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle5() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(20);
        line.add(22);
        line.add(21);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(4);
        line.add(11);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, true);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(16);
        line.add(17);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, true);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " ' ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle6() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        line.add(4);
        line.add(10);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(20);
        line.add(22);
        line.add(21);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(16);
        line.add(17);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(25);
        line.add(26);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " | ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle7() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        line.add(3);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(5);
        line.add(6);
        line.add(9);
        line.add(8);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, true);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);


        prefEditor.putString("overlay.divider", ";");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle8() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(3);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(4);
        line.add(10);
        line.add(16);
        line.add(17);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, true);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", "*");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle9() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        line.add(16);
        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 15);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(17);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(4);
        line.add(19);
        line.add(11);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() / 3));
        prefEditor.putString("overlay.y_pos.line" + lineno, "40");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 15);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " - ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle10() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 15);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(4);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(3);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(10);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "40");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 14);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " <> ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle11() {


        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        lineno = 0;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(20);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(4);
        line.add(11);
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "2");
        prefEditor.putInt("overlay.font.size.line" + lineno, 18);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, true);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0xB0000000);

        prefEditor.putString("overlay.divider", " | ");
        prefEditor.commit();

        DGoverlay.initReset();
    }

    public void setStyle12() {

        int lineno;
        ArrayList<Integer> line = new ArrayList<Integer>();

        lineno = 0;
        line.add(4);
        line.add(11);
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(16);
        line.add(17);
        lineno = 1;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "15");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        line.add(2);
        lineno = 2;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, "1");
        prefEditor.putString("overlay.y_pos.line" + lineno, String.valueOf(display.getHeight()));
        prefEditor.putBoolean("overlay.align.right.line" + lineno, false);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 17);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        line.clear();
        lineno = 3;
        prefEditor.putString("layout.line" + lineno, ObjectSerializer.serialize(line));
        prefEditor.putString("overlay.x_pos.line" + lineno, String.valueOf(display.getWidth() - 1));
        prefEditor.putString("overlay.y_pos.line" + lineno, "40");
        prefEditor.putBoolean("overlay.align.right.line" + lineno, true);
        prefEditor.putInt("overlay.color.normal.line" + lineno, -16318720);
        prefEditor.putInt("overlay.color.alert.line" + lineno, -256);
        prefEditor.putString("overlay.font.type.line" + lineno, "1");
        prefEditor.putInt("overlay.font.size.line" + lineno, 16);
        prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
        prefEditor.putInt("overlay.color.background.line" + lineno, 0x70000000);

        prefEditor.putString("overlay.divider", " ~ ");
        prefEditor.commit();

        DGoverlay.initReset();
    }
}
