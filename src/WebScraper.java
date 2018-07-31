import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;

// ALL THIS ONLY WORKS FOR BASKETBALL FROM AMAZON RESULTS
// USE THE TEXT FILE TO GET THE DEMONSTRATION WEBSITES THAT WORK
// THE NAME OF THE PRODUCT AND ITS PRICE PRINT OUT TO A FILE CALLED AmazonOutput.xls
// THE RESULTS ARE ALREADY IN ORDER FROM LOWEST TO GREATEST
// CHANGE THE FILE DESTINATION IF IT DOESN'T BECAUSE I DON'T KNOW YOUR COMPUTER DESTINATIONS
// RIGHT NOW IT GOES TO THE C Drive

public class WebScraper {
	
	public static String getName(int resultItem, Elements body) {
		String price = "";
		for (Element stepper : body) {
			String tempPrice = stepper.select("li#result_" + resultItem + " div.s-item-container div.a-row.a-spacing-mini div.a-row.a-spacing-none a.a-link-normal h2").text();
			price = tempPrice;
		}
		return price;
	}

	public static String getPrice(int resultItem, Elements body) {
		String price = "";
		for (Element stepper : body) {
			String tempPrice = stepper.select("li#result_" + resultItem + " div.s-item-container div.a-row.a-spacing-mini div.a-row.a-spacing-none a.a-link-normal.a-text-normal span.a-offscreen").text();
			price = tempPrice;
		}
		return price;
	}
	
	public static String getRating(int resultItem, Elements body) {
		String price = "";
		for (Element stepper : body) {
			String tempPrice = stepper.select("li#result_" + resultItem + " div.s-item-container div.a-row.a-spacing-none span span.a-declarative a.a-popover-trigger.a-declarative i.a-icon span.a-icon-alt").text();
			price = tempPrice;
		}
		return price;
	}
	
	public static int formatResultsOnPage_Small(String num) {
		int findDash = num.indexOf("-");
		String reducedNumPage = num.substring(0, findDash);
		return Integer.parseInt(reducedNumPage);
	}
	
	public static int formatResultsOnPage_Large(String num) {
		int findDash = num.indexOf("-");
		int findLetter = num.indexOf("o");
		String reducedNumPage = num.substring(findDash + 1, findLetter - 1);
		return Integer.parseInt(reducedNumPage);
	}
	
	
	public static String formatPrice(String price) {
		String returnStatement = "";
		if (price.contains("[") == true) {
			int findCloseBracket = price.indexOf("]");
			returnStatement = price.substring(findCloseBracket + 2);
		}
		else {
			returnStatement = price;
		}
		return returnStatement;
	}
	 public static void main(String[] args) throws IOException {
		 
		Scanner reader = null;
		 
		 boolean inputFinished = false;
		 
		 ArrayList<String> idOfItem = new ArrayList<String>();
			ArrayList<String> nameOfItem = new ArrayList<String>();
			ArrayList<String> priceOfItem = new ArrayList<String>();
			ArrayList<String> ratingOfItem = new ArrayList<String>();
		 
		 while (inputFinished == false) {
			 
			reader = new Scanner(System.in);
			System.out.print("Enter the website: ");
			String input = reader.nextLine();

			final String QUIT = "quit";

			if (input.equalsIgnoreCase(QUIT)) {
				inputFinished = true;
			}
			else {

				Document amazonDoc = Jsoup.connect(input).get();

				Elements body_topDynamicContent = amazonDoc.select("div#topDynamicContent");
				int smallNumOnPage = 0;
				int largeNumOnPage = 0;

				for (Element stepper : body_topDynamicContent) {
					smallNumOnPage = formatResultsOnPage_Small(stepper
							.select("div#s-result-info-bar div#s-result-info-bar-content.a-row div.a-column div.s-first-column div.a-section span#s-result-count")
							.text());
					largeNumOnPage = formatResultsOnPage_Large(stepper
							.select("div#s-result-info-bar div#s-result-info-bar-content.a-row div.a-column div.s-first-column div.a-section span#s-result-count")
							.text());
				}

				Elements body = amazonDoc.select("div#resultsCol");

				for (int i = smallNumOnPage - 1; i < largeNumOnPage; i++) {
					idOfItem.add("result_" + i);
					nameOfItem.add(getName(i, body));
					priceOfItem.add(getPrice(i, body));
					ratingOfItem.add(getRating(i, body));
				}

				System.out.println(idOfItem.toString());
				System.out.println(nameOfItem.toString());
				System.out.println(priceOfItem.toString());
				// System.out.println(ratingOfItem.toString());
				
			}
			
			
		}

		 System.out.println("while loop ended");
		 
		 WritableWorkbook workbook;
			
			try {
				
				workbook = Workbook.createWorkbook(new File("C:\\AmazonOutput.xls"));
				WritableSheet sheet = workbook.createSheet("Main", 0);

				Label nameLabel = new Label(0, 0, "Name Of Item");
				Label priceLabel = new Label(1, 0, "Price Of Item");
				
				sheet.addCell(nameLabel);
				sheet.addCell(priceLabel);
				
				int counter_name = 0;
				int placementCounter_name = 1;
				Label tempLabel_name = null;
				
				while (counter_name < nameOfItem.size()) {
					tempLabel_name = new Label(0, placementCounter_name, nameOfItem.get(counter_name));
					sheet.addCell(tempLabel_name);
					placementCounter_name++;
					counter_name++;
				}
				
				int counter_price = 0;
				int placementCounter_price = 1;
				Label tempLabel_price = null;
				
				while (counter_price < priceOfItem.size()) {
					tempLabel_price = new Label(1, placementCounter_price,priceOfItem.get(counter_price));
					sheet.addCell(tempLabel_price);
					placementCounter_price++;
					counter_price++;
				}
				
				//Label testLabel = new Label(0, 0, "Test");
				//sheet.addCell(testLabel);
				workbook.write();
				workbook.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		 
/*		Document amazonDoc = Jsoup.connect("https://www.amazon.com/s/ref=sr_nr_n_1?fst=as%3Aoff&rh=n%3A3396751%2Ck%3Abasketball&sort=price-asc-rank&keywords=basketball&ie=UTF8&qid=1532976164&rnid=2941120011").get(); //amazon basketball doc	
		 //Elements body = amazonDoc.select("ul#results-list-atf");
		 // this results produces only the first 5 ratings and prices for an uknnown reason
		 
		Elements body = amazonDoc.select("div#resultsCol");
		 
		for (Element stepper : body) {
			 String tempName = stepper.select("div#centerMinus div#atfResults.a-row.s-result-list-parent-container ul#s-results-list-atf.s-result-list li#result_8.s-result-item.celwidget div.s-item-container div.a-row.a-spacing-mini div.a-row.a-spacing-none a.a-link-normal.s-access-detail-page.s-color-twister-title-link.a-text-normal h2").text();
			 System.out.println(tempName);
			 //String result = stepper.select("li.s-result-item div.s-item-container div.a-row.a-spacing-none span span.a-declarative a.a-popover-trigger.a-declarative i.a-icon.a-icon-star span.a-icon-alt").text();
			// System.out.println(result);
		 }
		 
		 ArrayList<String> idOfItem = new ArrayList<String>();
			ArrayList<String> nameOfItem = new ArrayList<String>();
			ArrayList<String> priceOfItem = new ArrayList<String>();
			ArrayList<String> ratingOfItem = new ArrayList<String>();
			 
			for (int i = 0; i < 30; i++) {
				idOfItem.add("result_" + i);
				nameOfItem.add(getName(i, body));
				priceOfItem.add(getPrice(i, body));
				ratingOfItem.add(getRating(i, body));
			}
			
			System.out.println(idOfItem.toString());
			System.out.println(nameOfItem.toString());
			System.out.println(priceOfItem.toString());
			System.out.println(ratingOfItem.toString());
			
			WritableWorkbook workbook;
			
			try {
				
				workbook = Workbook.createWorkbook(new File("E:\\AmazonOutput.xls"));
				WritableSheet sheet = workbook.createSheet("Main", 0);

				Label nameLabel = new Label(0, 0, "Name Of Item");
				Label priceLabel = new Label(1, 0, "Price Of Item");
				
				sheet.addCell(nameLabel);
				sheet.addCell(priceLabel);
				
				int counter_name = 0;
				int placementCounter_name = 1;
				Label tempLabel_name = null;
				
				while (counter_name < nameOfItem.size()) {
					tempLabel_name = new Label(0, placementCounter_name, nameOfItem.get(counter_name));
					sheet.addCell(tempLabel_name);
					placementCounter_name++;
					counter_name++;
				}
				
				int counter_price = 0;
				int placementCounter_price = 1;
				Label tempLabel_price = null;
				
				while (counter_price < priceOfItem.size()) {
					tempLabel_price = new Label(1, placementCounter_price, priceOfItem.get(counter_price));
					sheet.addCell(tempLabel_price);
					placementCounter_price++;
					counter_price++;
				}
				
				Label testLabel = new Label(0, 0, "Test");
				sheet.addCell(testLabel);
				workbook.write();
				workbook.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		 
	       /* String url = "https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords=basketballs";
	        Document document = Jsoup.connect(url).get();

	        String question = document.select("#question .post-text").text();
	        System.out.println("Question: " + question);

	        Elements answerers = document.select("#answers .user-details a");
	        for (Element answerer : answerers) {
	            System.out.println("Answerer: " + answerer.text());
	        }
	        
	        int counter = 0;
	        ArrayList<Element> elementList = new ArrayList<>();	 
	        ArrayList<String> prices = new ArrayList<>();
	        
	        Element li = document.text("test");
	       
	        while (li != null) {
	    	   
	        	String tempText = "";
	        	tempText = document.getElementsByClass("sx-price-whole").text();
	        	tempText = tempText + "." + document.getElementsByClass("sx-price-fractional").text();
	        	prices.add(tempText);
	        	
	       }
	       
	       System.out.println("loop ended");
	       
	       for (int i = 0; i < prices.size(); i++) {
	    	   System.out.println(prices.get(i));
	       }*/
		 
		 /*final Document document = Jsoup.connect("https://www.imdb.com/chart/top").get();
		 
		 for (Element row : document.select("table.chart.full-width tr")) {
			final String title = row.select(".titleColumn a").text();
			final String rating = row.select(".imdbRating").text();
			
			System.out.println(title + " => " + rating);*/
		
	 	//}
		
		//Document doc = Jsoup.connect("https://www.amazon.com/gp/search/ref=sr_il_ti_sports-and-fitness?fst=as%3Aoff&rh=n%3A3375251%2Cn%3A10971181011%2Cn%3A706809011%2Cn%3A3396541%2Cn%3A3396751%2Ck%3Abasketballs&sort=price-asc-rank&keywords=basketballs&ie=UTF8&qid=1532821269&lo=sports-and-fitness").get();
		
		//Document ebayDoc = Jsoup.connect("https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313.TR11.TRC2.A0.H0.Xbasketball.TRS1&_nkw=basketball&_sacat=0").get(); // ebay basketball doc 
		 
		/*Elements body = ebayDoc.select("div#mainContent");
		
		for (Element stepper : body) {
			
			String price = stepper.select("div.srp-layout-inner div.srp-river-main div.srp-river-results.clearfix ul.srp-results.srp-list.clearfix li.s-item div.s-item__wrapper.clearfix div.s-item__info.clearfix div.s-item__details.clearfix div.s-item__details.s-item__detail--primary span.s-item__price").text();
			System.out.println(price);
			
		}*/
		 
		
		 
		/*int numOfResults = 0;
		boolean findEndOfResultsOnPage = false;
		
		ArrayList<String> idOfItem = new ArrayList<String>();
		ArrayList<String> nameOfItem = new ArrayList<String>();
		ArrayList<String> priceOfItem = new ArrayList<String>();
		ArrayList<String> ratingOfItem = new ArrayList<String>();
		
		while (findEndOfResultsOnPage == false) {
			if (amazonDoc.getElementById("result_" + numOfResults) != null) {
				idOfItem.add("result_" + numOfResults);
				nameOfItem.add(getName(numOfResults, body));
				priceOfItem.add(getPrice(numOfResults, body));
				numOfResults++;
			} else {
				findEndOfResultsOnPage = true;
			}
		}*/
		
		
		
		/*Elements body = doc.select("ul#s-results-list-atf");
		
		for (Element step : body) {
			String price = step.select("li.s-result-item  div.s-item-container div.a-spacing-mini div.a-spacing-none a.a-link-normal span.a-offscreen").text();
			System.out.println(price);
			String rating = step.select("li.s-result-item  div.s-item-container div.a-spacing-none span span.a-declarative a.a-popover-trigger i.a-icon span.a-icon-alt").text();
			System.out.println(rating);
			
			String price = step.select("li.s-result-item div.s-item-container div.a-spacing-none div.a-spacing-none a.a-link-normal span.a-offscreen").text();
			System.out.println(price);
			
		}*/
		 
		}
	
}
