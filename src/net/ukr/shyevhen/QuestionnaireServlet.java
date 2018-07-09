package net.ukr.shyevhen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuestionnaireServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String msg = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Result</title><style>@import url('style/tablestyle.css');</style></head><body>%s</body></html>";
	private static Map<String, String> users = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Integer> question = Collections.synchronizedMap(new HashMap<>());
	private static int counter = 0;

	@Override
	public void init() throws ServletException {
		question.put("x<=25", 0);
		question.put("25<x<=50", 0);
		question.put("50<x", 0);
		question.put("Elementary", 0);
		question.put("Pre-Intermediate", 0);
		question.put("Intermediate", 0);
		question.put("Upper-Intermediate", 0);
		question.put("Advanced", 0);
		question.put("Proficiency", 0);
		question.put("java", 0);
		question.put("c++", 0);
		question.put("python", 0);
		question.put("others", 0);
		question.put("learn", 0);
	}

	@Override
	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		String surname = req.getParameter("surname");
		String name = req.getParameter("name");
		if (!users.containsKey(surname) || !users.get(surname).equals(name)) {
			String ageS = req.getParameter("age");
			int age = 0;
			try {
				age = Integer.parseInt(ageS);
			} catch (NumberFormatException e) {
				incorrectInput(pw);
				return;
			}
			addAge(age);
			String language = req.getParameter("language");
			if (question.containsKey(language)) {
				question.put(language, question.get(language) + 1);
			}
			addProgLang(req);
			String learn = req.getParameter("learn");
			if (learn.equals("Yes") && question.containsKey("learn")) {
				question.put("learn", question.get("learn") + 1);
			}
			users.put(surname, name);
			counter += 1;
		}
		pw.println(String.format(msg, getResponse()));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		pw.println(String.format(msg, getResponse()));
	}

	private void incorrectInput(PrintWriter pw) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new FileReader(new File("../work/JavaProg/Lesson3HomeWorkEx1Pro/WebContent/index.html")))) {
			String text = "";
			for (; (text = br.readLine()) != null;) {
				sb.append(text).append(System.lineSeparator());
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		String respon = sb.toString();
		respon = respon.substring(0, respon.indexOf("</table>"))
				+ "<tr><th class='err' colspan='2'>Wrong input parameters"
				+ respon.substring(respon.indexOf("</table>"), respon.length());
		pw.print(respon);
	}

	private void addAge(int age) {
		if (age <= 25) {
			question.put("x<=25", question.get("x<=25") + 1);
		} else if (age > 25 && age <= 50) {
			question.put("25<x<=50", question.get("25<x<=50") + 1);
		} else if (age > 50) {
			question.put("50<x", question.get("50<x") + 1);
		}
	}

	private void addProgLang(HttpServletRequest req) {
		if (req.getParameter("checkbox1") != null) {
			question.put("java", question.get("java") + 1);
		}
		if (req.getParameter("checkbox2") != null) {
			question.put("c++", question.get("c++") + 1);
		}
		if (req.getParameter("checkbox3") != null) {
			question.put("python", question.get("python") + 1);
		}
		if (req.getParameter("checkbox4") != null) {
			question.put("others", question.get("others") + 1);
		}
	}

	private String getResponse() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table cellspacing='0'><tr><th colspan='2'>View Poll Results");
		sb.append("<tr><td>Total surveyed:<td>" + counter + "<tr><td class='title' colspan='2'>Age");
		sb.append("<tr><td>Under 25 years old:<td>" + question.get("x<=25") + "<tr><td>From 26 to 50 years:<td>"
				+ question.get("25<x<=50"));
		sb.append("<tr><td>Older than 50 years:<td>" + question.get("50<x") + "<tr><td colspan='2'>English level");
		sb.append("<tr><td>Elementary<td>" + question.get("Elementary") + "<tr><td>Pre-Intermediate<td>"
				+ question.get("Pre-Intermediate"));
		sb.append("<tr><td>Intermediate<td>" + question.get("Intermediate") + "<tr><td>Upper-Intermediate<td>"
				+ question.get("Upper-Intermediate"));
		sb.append("<tr><td>Advanced<td>" + question.get("Advanced") + "<tr><td>Proficiency<td>"
				+ question.get("Proficiency"));
		sb.append("<tr><td colspan='2'>Knows the programming languages<tr><td>Java<td>" + question.get("java"));
		sb.append("<tr><td>C++<td>" + question.get("c++") + "<tr><td>Python<td>" + question.get("python")
				+ "<tr><td>Others<td>" + question.get("others"));
		sb.append("<tr><td>People want to learn new program language<td>"
				+ (counter != 0 ? (question.get("learn") * 100) / counter + "%" : "0%") + "</table>");
		return sb.toString();
	}

}
