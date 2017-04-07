import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database
{
	
	static final String DRIVER = "com.mysql.jdbc.Driver";  
	static final String LOCATION = "jdbc:mysql://localhost:3306/movie_reccomender?useSSL=false"; //YOUR DATABASE NAME
	static final String USERNAME = "root";
	static final String PASSWORD = "password"; //SET AS YOUR PASSWORD
	   
	Statement statement = null;
	Connection connection = null;
	   
	public Database()
	{	
		try{
		      //Registering the JDBC DRIVER
		      Class.forName(DRIVER);

		      //Opening the connection to the database
		      System.out.print("Connecting to database...");
		      connection = DriverManager.getConnection(LOCATION, USERNAME, PASSWORD);
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
		createActorsTable();
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
	
	private void createActorsTable()
	{
		try {
			String sqlCreateActors = "create table movie_actors ("
    		  		+"movieID						int not null,"
    		  		+"actorID 						varchar(255),"
    		  		+"actorName						varchar(255),"
    		  		+"ranking						int,"
    		  		+"primary key (actorID, movieID));";
			
			System.out.print("Creating table actors...");
		    statement.executeUpdate(sqlCreateActors);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
}
