package analyser;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import graphs.Graph;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import metrics.Metrics;

//Main file for the application
public class CodeAnalyser extends Application {
	
	//Scene essentials
	Pane root = new Pane();
	Canvas canvas;
	GraphicsContext gc;
	Scene scene;
	
	//Main screen
	Label tipLabel = new Label("Please upload a Java file or workspace: ");
	Label descLabel = new Label("This application analyses any java code that you upload to it, "
			+ "and then shows any code smells, errors, metrics and allows you to view the source \n"
			+ "code in order to help developers with refactoring, so they can make their code as "
			+ "easy to read as possible.");
	Button uploadButton = new Button("Upload file or workspace");
	Button quitButton = new Button("Quit");

	//Selection Screen
	Button codeButton = new Button("Source Code");
	Button metricsButton = new Button("Metrics");
	Button smellsButton = new Button("Smells/Errors");
	Button backButton = new Button("Go Back");;
	Button toMetricsButton = new Button("Go Back");
	
	//Source Code
	TextArea code = new TextArea();
	ArrayList<Node> classButtons = new ArrayList<Node>();
	
	//Metrics
	TextArea metricsList = new TextArea();
	Button visualiseButton = new Button("Visualise Metrics");
	Button methodsButton = new Button("View Methods");
	Button fieldsButton = new Button("View Fields");
	
	//Arrays for different views
	ArrayList<Node> mainScreen = new ArrayList<Node>();
	ArrayList<Node> selectionScreen = new ArrayList<Node>();
	ArrayList<Node> sourceCodeScreen = new ArrayList<Node>();
	ArrayList<Node> metricsScreen = new ArrayList<Node>();
	ArrayList<Node> smellsScreen = new ArrayList<Node>();
	ArrayList<Node> visualisedScreen = new ArrayList<Node>();
	
	//Classes
	CodeReader reader = new CodeReader();
	Graph graph = new Graph();
	
	//EventHandler(s)
	EventHandler<ActionEvent> uploadEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			upload();
		}
	};
	EventHandler<ActionEvent> quitEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.exit(0);
		}
	};
	EventHandler<ActionEvent> backEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			code.clear();
			reader.clearFiles();
			showScreen(0);
		}
	};
	EventHandler<ActionEvent> viewSourceEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//Changes screen
			showScreen(2);

			//Creates button for every file read by CodeReader class
			for (int i = 0; i < reader.getAllFiles().size(); i++) {
				File file = reader.getAllFiles().get(i);
				Button button = new Button(file.getName().replace(".java", ""));
				button.setLayoutX(25);
				button.setLayoutY((i * 30) + 85);
				
				//Event for display source code
				EventHandler<ActionEvent> codeEvent = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						try {
							//Reads each line from class
							String text = "";
							int lineNum = 1;
							Scanner scanner = new Scanner(file);
							while (scanner.hasNextLine()) {
								text += lineNum + "		" + scanner.nextLine() + "\n";
								lineNum++;
							}
							
							code.setText(text);
							scanner.close();
						}
						catch (Exception e) {
							showMessage("No source code found.");
						}
					}
				};

				button.setOnAction(codeEvent);
				root.getChildren().add(button);
			}
		}
	};
	
	EventHandler<ActionEvent> viewMetricsEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//Changes screen
			showScreen(3);
			
			//Creates button for every file read by CodeReader class
			for (int i = 0; i < reader.getAllMetrics().size(); i++) {
				Metrics metric = reader.getAllMetrics().get(i);
				Button button = new Button(metric.getFileName());
				button.setLayoutX(25);
				button.setLayoutY((i * 30) + 85);
				
				//Event for display source code
				EventHandler<ActionEvent> metricsEvent = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						String text = "Name:						" + metric.getFileName() + "\n";
						text += "Type:						" + metric.getType() + "\n";
						text += "Total Lines:					" + metric.getTotalLines() + "\n";
						text += "Total Comment Lines:			" + metric.getCommentLines() + "\n";
						text += "Total Methods:				" + metric.getMethods().size() + "\n";
						text += "Total Fields:					" + metric.getFields().size() + "\n";
						metricsList.setText(text);

						//Event for viewing graphs
						EventHandler<ActionEvent> showGraphs = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								showScreen(5);
								Node pie = graph.generatePieChart(metric);
								root.getChildren().add(pie);
								Node bar = graph.generateBarChart(metric);
								root.getChildren().add(bar);
							}
						};
						
						EventHandler<ActionEvent> showFields = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String text = "";
								for (String f : metric.getFields().keySet()) {
									text += f + "\n";
								}
								
								if (text.length() > 0)
									metricsList.setText(text);
								else
									metricsList.setText("This file does not have any methods.");
							}
						};
						
						EventHandler<ActionEvent> showMethods = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String text = "";
								for (String m : metric.getMethods().keySet()) {
									text += m + "\n";
								}
								
								if (text.length() > 0)
									metricsList.setText(text);
								else
									metricsList.setText("This file does not have any fields.");
							}
						};
						
						visualiseButton.setOnAction(showGraphs);
						fieldsButton.setOnAction(showFields);
						methodsButton.setOnAction(showMethods);
					}
				};

				button.setOnAction(metricsEvent);
				root.getChildren().add(button);
			}
		}
	};
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//Sets scene
		scene = new Scene(root, 1280, 720);
		canvas = new Canvas(1280, 720);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		//Objects for main screen
		tipLabel.setScaleX(1.5);
		tipLabel.setScaleY(1.5);
		tipLabel.setLayoutX(102);
		tipLabel.setLayoutY(70);
		
		descLabel.setScaleX(1.5);
		descLabel.setScaleY(1.5);
		descLabel.setLayoutX(240);
		descLabel.setLayoutY(230);
		
		uploadButton.setPrefSize(canvas.getWidth() - 100, 100);
		uploadButton.setLayoutX(50);
		uploadButton.setLayoutY(100);
		uploadButton.setOnAction(uploadEvent);
		Font uploadFont = uploadButton.getFont();
		float uploadFSize = (float)uploadFont.getSize() + 10.0f;
		uploadButton.setFont(uploadFont.font(uploadFSize));

		quitButton.setScaleX(1.5);
		quitButton.setScaleY(1.5);
		quitButton.setLayoutX(25);
		quitButton.setLayoutY(650);
		quitButton.setOnAction(quitEvent);

		//Objects for selection screen
		codeButton.setPrefSize((canvas.getWidth() - 100) / 3, 50);
		codeButton.setLayoutX(12);
		codeButton.setLayoutY(20);
		codeButton.setOnAction(viewSourceEvent);
		
		metricsButton.setPrefSize((canvas.getWidth() - 100) / 3, 50);
		metricsButton.setLayoutX(442);
		metricsButton.setLayoutY(20);
		metricsButton.setOnAction(viewMetricsEvent);
		
		smellsButton.setPrefSize((canvas.getWidth() - 100) / 3, 50);
		smellsButton.setLayoutX(872);
		smellsButton.setLayoutY(20);
		smellsButton.setOnAction(viewMetricsEvent);

		backButton.setScaleX(1.5);
		backButton.setScaleY(1.5);
		backButton.setLayoutX(25);
		backButton.setLayoutY(650);
		backButton.setOnAction(backEvent);

		toMetricsButton.setScaleX(1.5);
		toMetricsButton.setScaleY(1.5);
		toMetricsButton.setLayoutX(25);
		toMetricsButton.setLayoutY(650);
		toMetricsButton.setOnAction(viewMetricsEvent);
		
		//Source code screen
		code.setLayoutX(265);
		code.setLayoutY(80);
		code.setPrefSize(1000, 600);
		code.setEditable(false);
		Font codeFont = code.getFont();
		float codeFSize = (float)codeFont.getSize() + 3.0f;
		code.setFont(codeFont.font(codeFSize));
		
		//Metrics screen
		metricsList.setLayoutX(265);
		metricsList.setLayoutY(80);
		metricsList.setPrefSize(1000, 550);
		metricsList.setEditable(false);
		Font metricsFont = metricsList.getFont();
		float metricsFSize = (float)metricsFont.getSize() + 10.0f;
		metricsList.setFont(metricsFont.font(metricsFSize));

		visualiseButton.setScaleX(1.5);
		visualiseButton.setScaleY(1.5);
		visualiseButton.setLayoutX(290);
		visualiseButton.setLayoutY(650);

		fieldsButton.setScaleX(1.5);
		fieldsButton.setScaleY(1.5);
		fieldsButton.setLayoutX(490);
		fieldsButton.setLayoutY(650);

		methodsButton.setScaleX(1.5);
		methodsButton.setScaleY(1.5);
		methodsButton.setLayoutX(690);
		methodsButton.setLayoutY(650);
		
		//Add elements to scene
		root.getChildren().add(canvas);

		mainScreen.add(tipLabel);
		mainScreen.add(descLabel);
		mainScreen.add(uploadButton);
		mainScreen.add(quitButton);
		
		selectionScreen.add(codeButton);
		selectionScreen.add(metricsButton);
		selectionScreen.add(smellsButton);
		selectionScreen.add(backButton);

		sourceCodeScreen.add(codeButton);
		sourceCodeScreen.add(metricsButton);
		sourceCodeScreen.add(smellsButton);
		sourceCodeScreen.add(backButton);
		sourceCodeScreen.add(code);
		
		metricsScreen.add(codeButton);
		metricsScreen.add(metricsButton);
		metricsScreen.add(smellsButton);
		metricsScreen.add(backButton);
		metricsScreen.add(metricsList);
		metricsScreen.add(visualiseButton);
		metricsScreen.add(methodsButton);
		metricsScreen.add(fieldsButton);
		
		visualisedScreen.add(toMetricsButton);
		
		smellsScreen.add(codeButton);
		smellsScreen.add(metricsButton);
		smellsScreen.add(smellsButton);
		smellsScreen.add(backButton);
		
		primaryStage.setTitle("Code Analyser");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		showScreen(0);
	}
	
	/**
	 * Switches between the different views by clearing
	 * the scene of all objects, then adding the objects
	 * from the desired view
	 * 
	 * @param screen
	 * @return nothing
	 */
	private void showScreen(int screen) {
		root.getChildren().clear();
		//Main
		if (screen == 0) {
			root.getChildren().addAll(mainScreen);
		}
		//Selection
		else if (screen == 1) {
			root.getChildren().addAll(selectionScreen);
		}
		//Source Code
		else if (screen == 2) {
			root.getChildren().addAll(sourceCodeScreen);
		}
		//Metrics
		else if (screen == 3) {
			root.getChildren().addAll(metricsScreen);
		}
		//Smells
		else if (screen == 4) {
			root.getChildren().addAll(smellsScreen);
		}
		//Visualised Metrics
		else if (screen == 5) {
			root.getChildren().addAll(visualisedScreen);
		}
	}
	
	/**
	 * The method that uploads a class/workspace
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void upload() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.showOpenDialog(fileChooser);
		File file = fileChooser.getSelectedFile();
		
		//Exits method if user doesn't select file
		if (file == null)
			return;
		
		int i = file.getName().lastIndexOf(".");
		String ext = "";
		
		//Directories will return -1 since there's no dots
		if (i >= 0)
			ext = file.getName().substring(i + 1);
		else
			ext = "directory";
		
		//Java file
		if (ext.equalsIgnoreCase("java"))
			reader.addFile(file);
		//Directory
		else if (ext.equalsIgnoreCase("directory"))
			reader.addFromDirectory(file.getPath());
		//Not compatible
		else {
			showMessage("You can only select Java files or directories.");
			return;
		}
		
		//Goes to next screen if it finds java files
		if (reader.getAllFiles().size() > 0)
		{
			reader.readAllFiles();
			showScreen(1);
		}
		else
			showMessage("No java files found.");
	}
	
	/**
	 * Displays message to the user
	 * 
	 * @param message
	 * @return nothing
	 */
	void showMessage(String message) {
		JOptionPane alert = new JOptionPane();
		alert.showMessageDialog(alert, message);
	}
}