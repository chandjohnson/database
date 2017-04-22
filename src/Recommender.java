import java.util.Scanner;

public class Recommender
{
	static Database db;

	public static void Main(String[] args)
	{
		db = new Database();

		/*
		 * Leave these methods commented out unless you want to re-create the
		 * database, warning this will take a long time depending on the speed
		 * of your machine.
		 */

		// db.createTables();
		// db.migrateTables();
		// commandLineInput();

		Gui gui = new Gui(args);
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
