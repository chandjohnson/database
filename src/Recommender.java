public class Recommender
{
	public static void main(String[] args)
	{
		Database db = new Database();
		db.createTables();
		db.populateTables();
	}
}
