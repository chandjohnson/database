import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Gui extends Application
{
	public Gui(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("/recommender.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Title");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
