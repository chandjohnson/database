import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
	
	static final String DRIVER = "com.mysql.jdbc.Driver";  
	static final String LOCATION = "jdbc:mysql://localhost:3306/movie_recommender?useSSL=false"; //YOUR DATABASE NAME
	static final String USERNAME = "root";
	static final String PASSWORD = "windowlicker"; //SET AS YOUR PASSWORD
	   
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
			
			//Create each table here
			createMoviesTable();
			createGenresTable();
			createDirectorsTable();
			createActorsTable();
			createCountriesTable();
			createLocationsTable();
			createTagsTable();
			createMovieTagsTable();
			createUserTaggedTable();
			createTaggedMoviesTable();
			createRatedMoviesTable();
			
			//Populate each table here
			loadMovies();
			
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void loadMovies()
	{
		//Load the data from the .dat files here

		System.out.print("Pupulating movies table (this may take some time)...");
		try
		{
			BufferedReader br;
			br = new BufferedReader(new FileReader("Rotten Tomatos Dataset/movies.dat"));
			String line;
			
			String sqlEmptyTable = "DELETE FROM movies;";
			statement.executeUpdate(sqlEmptyTable);
			
			br.readLine();
			
			while ((line = br.readLine()) != null)
			{
				String [] dataChunk = line.replace("\'", "\\'").replace("\"", "\\\"").split("\t");
				if (dataChunk.length == 21)
				{
					String sqlInsert = "INSERT INTO movies VALUES (" + dataChunk[0] +", \'" + dataChunk[1] + "\', " + dataChunk[2] + ", \'" + dataChunk[3] + "\', \'" + dataChunk[4] + "\', " + dataChunk[5] + ", \'" + dataChunk[6] + "\', " + dataChunk[7] + ", " + dataChunk[8] + ", " + dataChunk[9] + ", " + dataChunk[10] + ", " + dataChunk[11] + ", " + dataChunk[12] + ", " + dataChunk[13] + ", " + dataChunk[14] + ", " + dataChunk[15] + ", " + dataChunk[16] + ", " + dataChunk[17] + ", " + dataChunk[18] + ", " + dataChunk[19] + ", \'" + dataChunk[20] + "\');";
					statement.executeUpdate(sqlInsert);
				}
				else
				{
					System.out.println("Omitted unformatted tuple " + line);
				}
			}
			br.close();
			System.out.println("[OK]");
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
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
			String sqlCreateActors = "CREATE TABLE movie_actors ("
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
	
	private void createCountriesTable()
	{
		try {
			String sqlCreateCountries = "CREATE TABLE movie_countries ("
    			+"movieID					int	not null,"
    		  	+"country 					varchar(255),"
    		  	+"primary key (movieID));";
			
			System.out.print("Creating table countries...");
		    statement.executeUpdate(sqlCreateCountries);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createLocationsTable()
	{
		try {
			String sqlCreateLocations = "CREATE TABLE movie_locations ("
    		  	+"movieID						int	not null,"
    		  	+"location1 					varchar(255),"
    		  	+"location2						varchar(255),"
    		  	+"location3						varchar(255),"
    		  	+"location4						varchar(255),"
    		  	+"primary key (movieID, location1, location2, location3, location4));";
			
			System.out.print("Creating table locations...");
		    statement.executeUpdate(sqlCreateLocations);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createTagsTable()
	{
		try {
			String sqlCreateTags = "CREATE TABLE tags ("
    		  		+"id					int	not null,"
    		  		+"value 				varchar(255),"
    		  		+"primary key (id));";
			
			System.out.print("Creating table tags...");
		    statement.executeUpdate(sqlCreateTags);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createMovieTagsTable()
	{
		try {
			String sqlCreateMovieTags = "CREATE TABLE movie_tags ("
    		  		+"movieID				int	not null,"
    		  		+"tagID 				int,"
    		  		+"tagWeight				int,"
    		  		+"primary key (movieID, tagID));";
			
			System.out.print("Creating table movie tags...");
		    statement.executeUpdate(sqlCreateMovieTags);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createUserTaggedTable()
	{
		try {
			String sqlCreateTaggedTable = "CREATE TABLE user_taggedmovies_timestamps ("
    		  		+"userID					int	not null,"
    		  		+"movieID   				int,"
    		  		+"tagID  					int,"
    		  		+"timestamp					long,"
    		  		+"primary key (userID, movieID, tagID));";
			
			System.out.print("Creating table user tagged...");
		    statement.executeUpdate(sqlCreateTaggedTable);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createTaggedMoviesTable()
	{
		try {
			String sqlTaggedMoviesTable = "CREATE TABLE user_taggedmovies ("
    		  		+"userID					int	not null,"
    		  		+"movieID					int,"
    		  		+"tagID						int,"
    		  		+"date_day					int,"
    		  		+"date_month				int,"
    		  		+"date_year					int,"
    		  		+"date_hour					int,"
    		  		+"date_minute				int,"
    		  		+"date_second				int,"
    		  		+"primary key (userID, movieID, tagID));";
			
			System.out.print("Creating table tagged movies...");
		    statement.executeUpdate(sqlTaggedMoviesTable);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}
	
	private void createRatedMoviesTable()
	{
		try {
			String sqlRatedMovies = "CREATE TABLE user_ratedmovies ("
	  		  		+"userID					int	not null,"
	  		  		+"movieID					int,"
	  		  		+"rating					double,"
	  		  		+"date_day					int,"
	  		  		+"date_month				int,"
	  		  		+"date_year					int,"
	  		  		+"date_hour					int,"
	  		  		+"date_minute				int,"
	  		  		+"date_second				int,"
	  		  		+"primary key (userID, movieID));";
			
			System.out.print("Creating table rated movies...");
		    statement.executeUpdate(sqlRatedMovies);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[WARN] " + e.toString());
		}
	}

}
