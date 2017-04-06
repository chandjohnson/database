import java.sql.*;

public class Reccomender
{
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost:3306/movie_reccomender?useSSL=false"; //YOUR DATABASE NAME

	   static final String USER = "root";
	   static final String PASS = "windowlicker"; //SET AS YOUR PASSWORD
	   
	   Statement statement = null;
	   Connection connection = null;
	
	public static void main(String[] args)
	{
		Reccomender reccomender = new Reccomender();
		reccomender.initDB();
		reccomender.loadData();
	}
	
	private void initDB()
	{	
		try{
		      //Registering the JDBC DRIVER
		      Class.forName(JDBC_DRIVER);

		      //Opening the connection to the database
		      System.out.print("Connecting to database...");
		      connection = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("[OK]");

		      //Initializing the database
		      System.out.print("Creating database...");
		      statement = connection.createStatement();
		      System.out.println("[OK]");
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
		
		//Create each table here
		createMoviesTable();
		createGenresTable();
		createDirectorsTable();
	}
	
	private void loadData()
	{
		//Load the data from the .dat files here
		
	}
	
	private void createMoviesTable()
	{
		try {
			String sqlCreateMovies = "CREATE TABLE movies ("
	    		+"id							int	not null,"
	    		+"title							varchar(255),"
	    		+"imdbID						int,"
	    		+"spanishTitle					varchar(255),"
	    		+"imdbPictureURL				varchar(255),"
	    		+"year							int,"
	    		+"rtID							varchar(255),"
	    		+"rtAllCriticsRating			double,"
	    		+"rtAllCriticsNumReviews		int,"
	    		+"rtAllCriticsNumFresh			int,"
	    		+"rtAllCriticsNumRotten			int,"
	    		+"rtAllCriticsScore				int,"
	    		+"rtTopCriticsRating			double,"
	    		+"rtTopCriticsNumReviews		int,"
	    		+"rtTopCriticsNumFresh			int,"
	    		+"rtTopCriticsNumRotten			int,"
	    		+"rtTopCriticsScore				int,"
	    		+"rtAudienceRating				double,"
	    		+"rtAudienceNumRatings			int,"
	    		+"rtAudienceScore				double,"
	    		+"rtPictureURL 					varchar(255),"
	    		+"primary key (id));";
			
			System.out.print("Creating table movies...");
		    statement.executeUpdate(sqlCreateMovies);
		    System.out.println("[OK]");
	      
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createGenresTable()
	{
		try {
			String sqlCreateGenres = "CREATE TABLE movie_genres ("
    		  		+"movieID			int	not null,"
    		  		+"genre 			varchar(255),"
    		  		+"primary key (movieID, genre));";
			
			System.out.print("Creating table genres...");
		    statement.executeUpdate(sqlCreateGenres);
		    System.out.println("[OK]");
	      
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createDirectorsTable()
	{
		try {
			String sqlCreateDirectors = "CREATE TABLE movie_directors ("
    		  		+"movieID				int	not null,"
    		  		+"directorID			varchar(255),"
    		  		+"directorName			varchar(255),"
    		  		+"primary key (directorID, movieID));";
			
			System.out.print("Creating table diretors...");
		    statement.executeUpdate(sqlCreateDirectors);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
}
