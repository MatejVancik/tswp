package com.mv2studio.tswp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;

public class MaisCalendarParser {

	public static void parseCalendar(Context context, String path) throws NumberFormatException, IOException {
		ArrayList<TClass> classes = new ArrayList<TClass>();
		InputStream json = new FileInputStream(new File(path));
		BufferedReader br = new BufferedReader(new InputStreamReader(json));

		String line;
		String inLine;

		TClass predmet;

		while ((line = br.readLine()) != null) {

			if (line.equalsIgnoreCase("BEGIN:VEVENT")) {
				String name = null;
				String room = null;
				boolean isCviko = false;
				Date start = null;
				Date end = null;

				while (!(inLine = br.readLine()).equalsIgnoreCase("END:VEVENT")) {

					if (inLine.startsWith("SUMMARY")) {
						name = ((inLine.split(":")[1]).split("\\(")[0]).trim();

						String[] array = inLine.split("-");
						String type = array[array.length - 1].trim();
						String regExprCvic = "Seminár|Cvičenie|Cvičenie laboratórne";
						isCviko = type.matches(regExprCvic);
					}

					if (inLine.startsWith("LOCATION")) {
						room = (inLine.split("\\(")[1]).split("\\)")[0].trim();
					}

					if (inLine.startsWith("DTSTART")) {
						String[] datumCas = inLine.split(":")[1].split("T");
						int rok = Integer.valueOf(datumCas[0].substring(0, 4));
						int mesiac = Integer.valueOf(datumCas[0].substring(4, 6));
						int den = Integer.valueOf(datumCas[0].substring(6, 8));
						int hodina = Integer.valueOf(datumCas[1].substring(0, 2));
						int minuta = Integer.valueOf(datumCas[1].substring(2, 4));
						start = new Date(rok - 1900, mesiac - 1, den, hodina, minuta);
					}

					if (inLine.startsWith("RRULE")) {
						String frequency = inLine.split("=")[1].split(";")[0].trim();
						String until = inLine.split("UNTIL=")[1];
						String byDay = until.split("=")[1];
						String datumCas[] = until.split(";")[0].split("T");
						int rok = Integer.valueOf(datumCas[0].substring(0, 4));
						int mesiac = Integer.valueOf(datumCas[0].substring(4, 6));
						int den = Integer.valueOf(datumCas[0].substring(6, 8));
						int hodina = Integer.valueOf(datumCas[1].substring(0, 2));
						int minuta = Integer.valueOf(datumCas[1].substring(2, 4));

						end = new Date(rok - 1900, mesiac - 1, den, hodina, minuta);
						predmet = new TClass(name, room, start, end, isCviko, true);
						System.out.println(predmet.getName());
					}

				}
				classes.add(new TClass(name, room, start, end, isCviko, true));
			}
		}
		new Db(context).insertClasses(classes);

	}

	// public static void readAsset(Context context) {
	// StringBuilder buf = new StringBuilder();
	//
	// try {
	// InputStream json = context.getAssets().open("report0.ICS");
	//
	// BufferedReader in = new BufferedReader(new InputStreamReader(json));
	// String str;
	//
	// while ((str = in.readLine()) != null) {
	// buf.append(str);
	// }
	// in.close();
	//
	// parseCalendar(buf.toString());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}
