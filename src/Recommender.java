import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class Recommender extends Application implements Initializable
{
	@FXML
	TextArea infoBox;
	@FXML
	Button searchBtn;
	@FXML
	ImageView rtImage;
	@FXML
	ImageView imdbImage;
	@FXML
	TextField searchBar;
	@FXML
	Text limitResultsText;
	@FXML
	TextField limitResults;
	@FXML
	ListView<String> movieList;
	@FXML
	ChoiceBox<String> searchBy;

	static Database db;
	private String res;
	private int mode = 0;
	
	public static void main(String[] args)
	{
		db = new Database();
		
		Scanner scanner = new Scanner(System.in);
		String x = "";
		do
		{
			System.out.print("\nDo you want to migrate the database Y/N? ");
			x = scanner.nextLine();
		} while(!x.equals("Y") && !x.equals("N"));
		
		if(x.equals("Y"))
		{
			do
			{
				System.out.print("WARNING, this will possibly take a long time are you sure? Y/N ");
				x = scanner.nextLine();
			} while(!x.equals("Y") && !x.equals("N"));
			
			if(x.equals("Y"))
			{
				System.out.println();
				db.createTables();
				db.migrateTables();
			}
		}
		scanner.close();

		launch(args);

	}

	public void onClickSearchBtn(ActionEvent event)
	{
		int index = searchBy.getSelectionModel().getSelectedIndex();
		int k = 0;
		String s = searchBar.getText();
		
		try
		{
			k = Integer.parseInt(limitResults.getText());
		} catch (NumberFormatException e)
		{
			return;
		}
		
		if(k == 0 && s.equals("")) return;
		
		imdbImage.setImage(null);
		rtImage.setImage(null);
		
		mode = index;
		
		if (index == 0)	res = db.query("SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies ORDER BY rtAudienceScore DESC LIMIT " + k);
		else if (index == 1) res = db.query("SELECT DISTINCT title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL, t.value FROM movies m, tags t, user_taggedmovies_timestamps u WHERE m.title LIKE \'%"+ s + "%\' AND m.id = u.movieID AND u.tagID = t.id ORDER BY title");
		else if (index == 2) res = db.query("SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies, movie_genres g WHERE g.movieID = id AND genre LIKE \'%" + s + "%\' ORDER BY rtAudienceScore DESC LIMIT " + k);
		else if (index == 3) res = db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_directors d WHERE d.directorName LIKE \'%"+ s + "%\' AND m.id = d.movieID ORDER BY year");
		else if (index == 4) res = db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_actors a WHERE a.actorName LIKE \'%"+ s + "%\' AND m.id = a.movieID ORDER BY year");
		else if (index == 5) res = db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, tags t, movie_tags mt WHERE t.value LIKE \'%"+ s + "%\' AND m.id = mt.movieID AND mt.tagID = t.id ORDER BY rtAudienceScore DESC");
		else if (index == 6) res = db.query("SELECT DISTINCT d.directorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_directors d "
			+ "WHERE d.movieID = m.id group by directorName having count(m.id) >= " + k
			+ " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");
		else if (index == 7) res = db.query("SELECT DISTINCT a.actorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_actors a WHERE a.movieID = m.id group by a.actorName having count(m.id) >= "
			+ k + " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");
		else if (index == 8) res = db.query("SELECT DISTINCT m.title, r.rating, g.genre, r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, "
			+ "r.date_second FROM movies m, movie_genres g, user_ratedmovies r WHERE r.movieID = m.id "
			+ "AND g.movieID = r.movieID AND r.userID = " + k
			+ " ORDER BY r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, r.date_second");
		else if (index == 9) res = db.query("SELECT DISTINCT t.value FROM tags t, movie_tags mt, movies m WHERE mt.tagID = t.id AND m.title = \'" + s + "\' AND m.id = mt.movieID");
		
		System.out.println(res);
		
		String resArr[] = res.split("\n");
		
		for(int i = 0; i < resArr.length; ++i) resArr[i] = resArr[i].split("\t")[0];
		
		if(index == 1 || index == 8) resArr = new HashSet<String>(Arrays.asList(resArr)).toArray(new String[0]);
		
		if(!res.equals(""))
		{
			ObservableList<String> items = FXCollections.observableArrayList(resArr);
			movieList.setItems(items);
			movieList.getSelectionModel().selectFirst();
		}
	}

	public void initialize(URL location, ResourceBundle resources)
	{		
		searchBar.setVisible(false);
		
		limitResults.setText("10");
		
		searchBy.getItems().removeAll(searchBy.getItems());
		searchBy.setItems(FXCollections.observableArrayList(
				"How many top movies do you want to see?",
				"Type a movie to see pictures for that movie.",
				"Type in a genre to see the top movies for that genre.", //Need numerical validation
				"Type a director to see top movies by him or her.",
				"Type an actor to see movies that he or her acted in.",
				"Type a movie tag to see top movies for that specific tag.",
				"Show top 10 directors that have made at least this number of movies.",
				"Show top 10 actors that have been in least this number of movies.",
				"Type a user ID to see the ratings for that particular user.",
				"Type a movie name to show all tags for that movie"
				));
		searchBy.getSelectionModel().selectFirst();
		
		searchBy.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> searchByChanged() );
	
		movieList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	movieListChanged();
		    }
		});
	}
	
	private void standardDisplay(String[] colArr)
	{
		infoBox.appendText("Year: " + colArr[1] + "\n");
		infoBox.appendText("Rating out of 100: " + colArr[2]);
		
		try
		{
			imdbImage.setImage(new Image(colArr[3].replaceAll(" ", "")));
		}
		catch(Exception e){}
		try
		{
			rtImage.setImage(new Image(colArr[4].replaceAll(" ", "")));
		}
		catch(Exception e){}
	}
	
	private void personDisplay(String[] colArr)
	{
		Float score = Float.parseFloat(colArr[1]);
		score = (float) Math.round(score * 10)/10;
		
		infoBox.appendText("Average Rotten Tomatoes score: " + score + "\n");
		infoBox.appendText("Movies: " + colArr[2]);
	}
	
	private void movieListChanged()
	{
		infoBox.setText("");
		int index = movieList.getSelectionModel().getSelectedIndex();
		
		System.out.println(index);
		
		if(movieList.getSelectionModel().getSelectedItems().size() == 0) return;
		
		if(index == -1) return;
		
		String resArr[] = res.split("\n");
		
		if(resArr.length < 1 || resArr.length - 1 < index) return;
		
		String colArr[] = resArr[index].split("\t");
		
		if(colArr.length < 1) return;
		
		if(mode == 1)
		{
			String movieName = movieList.getSelectionModel().getSelectedItem();
			boolean found = false;
			
			for(int i = 0; i < resArr.length; ++i)
			{
				String[] spl = resArr[i].split("\t");
				if(spl[0].equals(movieName))
				{
					if(!found)
					{
						infoBox.appendText("Year: " + spl[1] + "\n");
						infoBox.appendText("Rating out of 100: " + spl[2] + "\n");
						
						try
						{
							imdbImage.setImage(new Image(spl[3].replaceAll(" ", "")));
						}
						catch(Exception e){}
						try
						{
							rtImage.setImage(new Image(spl[3].replaceAll(" ", "")));
						}
						catch(Exception e){}
						
						found = true;
					}
					infoBox.appendText("User tag: " + spl[5] + "\n");
				}
			}
		}
		else if (mode == 6 || mode == 7) personDisplay(colArr);
		else if (mode == 8)
		{
			ArrayList<String> genres = new ArrayList<String>();
			ArrayList<String> total = new ArrayList<String>();
			
			for(int i = 0; i < resArr.length; ++i)
			{
				String[] splc = resArr[i].split("\t");
				
				if(!genres.contains(splc[2])) genres.add(splc[2]);
				
				total.add(splc[2]);
			}
			for(int i = 0; i < genres.size(); ++i)
			{
				String genre = genres.get(i);
				float percentage = (float)Collections.frequency(total, genre) / resArr.length;
				percentage = percentage * 100;
				percentage = (float) Math.round(percentage * 100.0) / 100;
				infoBox.appendText("Percentage of movies that are " + genre + ": " + percentage + "%\n");
			}
			
			String movieName = movieList.getSelectionModel().getSelectedItem();
			
			for(int i = 0; i < resArr.length; ++i)
			{
				colArr = resArr[i].split("\t");
				if(colArr[0].equals(movieName)) break;
			}
			
			infoBox.appendText("=======================\n");
			infoBox.appendText("User's rate out of 5: " + colArr[1] + "\n");
			infoBox.appendText("User's rated genre: " + colArr[2] + "\n");
			infoBox.appendText("User's rated date: " + colArr[4].replaceAll(" ", "") + "/" + colArr[5].replaceAll(" ", "") + "/" + colArr[3].replaceAll(" ", "") + " at " + colArr[6].replaceAll(" ", "") + ":" + colArr[7].replaceAll(" ", "") + "\n");
			
		}
		else standardDisplay(colArr);
	}
	
	private void searchByChanged()
	{
		int index = searchBy.getSelectionModel().getSelectedIndex();
		if(index == 0)
		{
			limitResults.setText("10");
			limitResultsText.setVisible(true);
			limitResultsText.setText("Limit Results");
			limitResults.setVisible(true);
			searchBar.setVisible(false);
		}
		else if(index == 1)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
		}
		else if(index == 2)
		{
			limitResults.setText("10");
			limitResultsText.setVisible(true);
			limitResultsText.setText("Limit Results");
			limitResults.setVisible(true);
			searchBar.setVisible(true);
		}
		else if(index == 3)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
		}
		else if(index == 4)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
		}
		else if(index == 5)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
		}
		else if(index == 6)
		{
			limitResults.setText("10");
			limitResultsText.setVisible(true);
			limitResultsText.setText("Movies");
			limitResults.setVisible(true);
			searchBar.setVisible(false);
		}
		else if(index == 7)
		{
			limitResults.setText("10");
			limitResultsText.setVisible(true);
			limitResultsText.setText("Movies");
			limitResults.setVisible(true);
			searchBar.setVisible(false);
		}
		else if(index == 8)
		{
			limitResults.setText("7612");
			limitResultsText.setVisible(true);
			limitResultsText.setText("User ID");
			limitResults.setVisible(true);
			searchBar.setVisible(false);
		}
		else if(index == 9)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
		}
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("/recommender.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("MovieFox");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void commandLineInput()
	{
		Scanner scanner = new Scanner(System.in);

		System.out.println("\nHow many top movies do you want to see?");
		String s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies ORDER BY rtAudienceScore DESC LIMIT "
						+ Integer.parseInt(s));

		System.out.println("\nType a movie to see pictures for that movie.");
		s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL, t.value FROM movies m, tags t, user_taggedmovies_timestamps u WHERE m.title LIKE \'%"
						+ s + "%\' AND m.id = u.movieID AND u.tagID = t.id ORDER BY title");

		System.out.println("\nType in a genre to see the top movies for that genre.");
		s = scanner.nextLine();
		System.out.println("\nHow many results would you LIKE?");
		int k = Integer.parseInt(scanner.nextLine());

		db.query(
				"SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies, movie_genres g WHERE g.movieID = id AND genre LIKE \'%"
						+ s + "%\' ORDER BY rtAudienceScore DESC LIMIT " + k);

		System.out.println("\nType a director to see top movies by him or her.");
		s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_directors d WHERE d.directorName LIKE \'%"
						+ s + "%\' AND m.id = d.movieID ORDER BY year");

		System.out.println("\nType an actor to see movies that he or her acted in.");
		s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_actors a WHERE a.actorName LIKE \'%"
						+ s + "%\' AND m.id = a.movieID ORDER BY year");

		System.out.println("\nType a movie tag to see top movies for that specific tag.");
		s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, tags t, movie_tags mt WHERE t.value LIKE \'%"
						+ s + "%\' AND m.id = mt.movieID AND mt.tagID = t.id ORDER BY rtAudienceScore DESC");

		System.out.println("\nShowing top directors, how many movies do they need to have made?");
		k = Integer.parseInt(scanner.nextLine());

		db.query("SELECT DISTINCT d.directorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_directors d "
				+ "WHERE d.movieID = m.id group by directorName having count(m.id) >= " + k
				+ " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");

		System.out.println("\nShowing top actors, how many movies do they need to have been in?");
		k = Integer.parseInt(scanner.nextLine());

		db.query(
				"SELECT DISTINCT a.actorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_actors a WHERE a.movieID = m.id group by a.actorName having count(m.id) >= "
						+ k + " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");

		System.out.println("\nType a user ID to show the ratings for that particular user.");
		k = Integer.parseInt(scanner.nextLine());

		db.query(
				"SELECT DISTINCT m.title, r.rating, g.genre, r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, "
						+ "r.date_second FROM movies m, movie_genres g, user_ratedmovies r WHERE r.movieID = m.id "
						+ "AND g.movieID = r.movieID AND r.userID = " + k
						+ " ORDER BY r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, r.date_second");

		System.out.println("\nType a movie name to show all tags for that movie");
		s = scanner.nextLine();

		db.query(
				"SELECT DISTINCT t.value FROM tags t, movie_tags mt, movies m WHERE mt.tagID = t.id AND m.title LIKE \'%"
						+ s + "%\' AND m.id = mt.movieID");
		scanner.close();
	}
}
