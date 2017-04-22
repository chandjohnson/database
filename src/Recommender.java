import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.ChoiceBox;

public class Recommender extends Application implements Initializable
{
	@FXML
	Button searchBtn;
	@FXML
	TextField searchBar;
	@FXML
	Text limitResultsText;
	@FXML
	TextField limitResults;
	@FXML
	ChoiceBox<String> searchBy;

	static Database db;

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

		launch(args);

	}

	public void onClickSearchBtn(ActionEvent event)
	{
		int index = searchBy.getSelectionModel().getSelectedIndex();
		String s = searchBar.getText();
		String k = limitResults.getText();
		
		if (index == 0) db.query("SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies ORDER BY rtAudienceScore DESC LIMIT " + k);
		else if (index == 1) db.query("SELECT DISTINCT title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL, t.value FROM movies m, tags t, user_taggedmovies_timestamps u WHERE m.title LIKE \'%"+ s + "%\' AND m.id = u.movieID AND u.tagID = t.id ORDER BY title");
		else if (index == 2) db.query("SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL FROM movies, movie_genres g WHERE g.movieID = id AND genre LIKE \'%" + s + "%\' ORDER BY rtAudienceScore DESC LIMIT " + k);
		else if (index == 3) db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_directors d WHERE d.directorName LIKE \'%"+ s + "%\' AND m.id = d.movieID ORDER BY year");
		else if (index == 4) db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, movie_actors a WHERE a.actorName LIKE \'%"+ s + "%\' AND m.id = a.movieID ORDER BY year");
		else if (index == 5) db.query("SELECT DISTINCT m.title, m.year, m.rtAudienceScore, m.rtPictureURL, m.imdbPictureURL FROM movies m, tags t, movie_tags mt WHERE t.value LIKE \'%"+ s + "%\' AND m.id = mt.movieID AND mt.tagID = t.id ORDER BY rtAudienceScore DESC");
		else if (index == 6) db.query("SELECT DISTINCT d.directorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_directors d "
			+ "WHERE d.movieID = m.id group by directorName having count(m.id) >= " + k
			+ " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");
		else if (index == 7) db.query("SELECT DISTINCT a.actorName, avg(m.rtAudienceScore), count(m.id) FROM movies m, movie_actors a WHERE a.movieID = m.id group by a.actorName having count(m.id) >= "
			+ k + " ORDER BY avg(rtAudienceScore) DESC LIMIT 10");
		else if (index == 8) db.query("SELECT DISTINCT m.title, r.rating, g.genre, r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, "
			+ "r.date_second FROM movies m, movie_genres g, user_ratedmovies r WHERE r.movieID = m.id "
			+ "AND g.movieID = r.movieID AND r.userID = " + k
			+ " ORDER BY r.date_year, r.date_month, r.date_day, r.date_hour, r.date_minute, r.date_second");
		else if (index == 9) db.query("SELECT DISTINCT t.value FROM tags t, movie_tags mt, movies m WHERE mt.tagID = t.id AND m.title LIKE \'%"
			+ s + "%\' AND m.id = mt.movieID");
	}

	public void initialize(URL location, ResourceBundle resources)
	{		
		searchBar.setVisible(false);
		
		searchBy.getItems().removeAll(searchBy.getItems());
		searchBy.setItems(FXCollections.observableArrayList(
				"How many top movies do you want to see?",
				"Type a movie to see pictures for that movie.",
				"Type in a genre to see the top movies for that genre.", //Need numerical validation
				"Type a director to see top movies by him or her.",
				"Type an actor to see movies that he or her acted in.",
				"Type a movie tag to see top movies for that specific tag.",
				"Show directors that have made at least this number of movies.",
				"Show actors that have been in least this number of movies.",
				"Type a user ID to see the ratings for that particular user.",
				"Type a movie name to show all tags for that movie"
				));
		searchBy.getSelectionModel().selectFirst();
		
		searchBy.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectionChanged() );
	}
	
	private void selectionChanged()
	{
		int index = searchBy.getSelectionModel().getSelectedIndex();
		if(index == 0)
		{
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
			limitResultsText.setVisible(true);
			limitResultsText.setText("Movies");
			limitResults.setVisible(true);
			searchBar.setVisible(true);
		}
		else if(index == 7)
		{
			limitResultsText.setVisible(true);
			limitResultsText.setText("Movies");
			limitResults.setVisible(true);
			searchBar.setVisible(true);
		}
		else if(index == 8)
		{
			limitResultsText.setVisible(false);
			limitResults.setVisible(false);
			searchBar.setVisible(true);
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
	}
}
