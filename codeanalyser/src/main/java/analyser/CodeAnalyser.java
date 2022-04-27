package analyser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import code_smells.CodeSmells;
import graphs.Graph;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import metrics.FieldMetrics;
import metrics.MethodMetrics;
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
			+ "and then it shows any code smells, errors, and metrics for each file and allows you to \n "
			+ "view the source code in order to help developers with refactoring, so they can make their code as "
			+ "easy to read as possible.");
	Button uploadButton = new Button("Upload new file/project");
	Button uploadRecentButton = new Button("Open most recent");
	Button githubConnect = new Button("Connect");
	TextArea githubUserBox = new TextArea();
	TextArea githubRepoBox = new TextArea();
	Label gitHubLabel = new Label("GitHub:");
	Button quitButton = new Button("Quit");

	//Selection Screen
	Button codeButton = new Button("Source Code");
	Button metricsButton = new Button("Metrics");
	Button smellsButton = new Button("Smells/Errors");
	Button overviewButton = new Button("Overview");
	Button backButton = new Button("Go Back");
	Button toMetricsButton = new Button("Go Back");
	TextArea codeOverview = new TextArea();

	//Source Code
	TextArea code = new TextArea();
	int sourcePage;
	
	//Metrics
	TextArea metricsList = new TextArea();
	Button visualiseButton = new Button("Visualise Metrics");
	Button methodsButton = new Button("View Methods");
	Button fieldsButton = new Button("View Fields");
	int metricsPage;
	
	//Smells
	ArrayList<CodeSmellNode> smellNodes = new ArrayList<CodeSmellNode>();
	ComboBox<String> smellDropDown = new ComboBox<String>();
	Button smellPrevPage = new Button("<-");
	Button smellNextPage = new Button("->");
	Label pageNum = new Label("1");
	Button refresh = new Button("Refresh");
	Label noSmellsLabel = new Label("No code smells detected");
	int smellPage;
	
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
	EventHandler<ActionEvent> githubEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			try {
				connectGithub();
			} 
			catch (IOException e) {
				showMessage("Cannot retrieve github repository.");
				System.out.println(e);
			}
		}};
	
	EventHandler<ActionEvent> prevSmellPageEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (smellPage > 0)
				smellPage--;
			
			renderSmell(smellPage);
		}};

		EventHandler<ActionEvent> nextSmellPageEvent = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (smellPage < (smellNodes.size() / 3))
					smellPage++;
				
				renderSmell(smellPage);
			}};
	
	EventHandler<ActionEvent> uploadEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.showOpenDialog(fileChooser);
			File file = fileChooser.getSelectedFile();
			upload(file);
		}
	};
	EventHandler<ActionEvent> openRecentEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			uploadFromRecent();
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
	EventHandler<ActionEvent> viewSelectionEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			showScreen(1);
		}
	};
	
	EventHandler<ActionEvent> viewSourceEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//Changes screen
			showScreen(2);
			int n = 0;

			//Creates button for every file read by CodeReader class
			for (int i = (sourcePage * 17); i < (sourcePage * 17) + 17; i++) {
				if (i > reader.getAllFiles().size() - 1)
					break;
				
				File file = reader.getAllFiles().get(i);
				Button button = new Button(file.getName().replace(".java", ""));
				button.setLayoutX(25);
				button.setLayoutY((n * 30) + 85);
				
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
							System.out.println(e);
						}
					}
				};

				button.setOnAction(codeEvent);
				root.getChildren().add(button);
				n++;
			}

			EventHandler<ActionEvent> nextEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (sourcePage < (reader.getAllFiles().size() / 17)) {
						sourcePage++;
						viewSourceEvent.handle(null);
					}
				}};

			EventHandler<ActionEvent> prevEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (sourcePage > 0) {
						sourcePage--;
						viewSourceEvent.handle(null);
					}
				}};
			
			Button prev = new Button("<-");
			prev.setLayoutY((17 * 30) + 85);
			prev.setLayoutX(25);
			prev.setOnAction(prevEvent);
			root.getChildren().add(prev);
			
			Button next = new Button("->");
			next.setLayoutY((17 * 30) + 85);
			next.setLayoutX(230);
			next.setOnAction(nextEvent);
			root.getChildren().add(next);
		}
	};
	
	EventHandler<ActionEvent> viewMetricsEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//Changes screen
			showScreen(3);
			int n = 0;
			
			//Creates button for every file read by CodeReader class
			for (int i = (metricsPage * 17); i < (metricsPage * 17) + 17; i++) {
				if (i > reader.getAllFiles().size() - 1)
					break;
				
				Metrics metric = reader.getAllMetrics().get(i);
				Button button = new Button(metric.getFileName());
				button.setLayoutX(25);
				button.setLayoutY((n * 30) + 85);
				
				//Event for display source code
				EventHandler<ActionEvent> metricsEvent = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						
						String text= "";
						if (metric.getType() != "aborted") {
							text += "Name:						" + metric.getFileName() + "\n";
							text += "Size:							" + metric.getSize() + "\n";
							text += "Type:						" + metric.getType() + "\n";
							text += "Total Lines:					" + metric.getTotalLines() + "\n";
							text += "Total Comment Lines:			" + metric.getCommentLines() + "\n";
							text += "Total Methods:				" + metric.getMethods().size() + "\n";
							text += "Total Fields:					" + metric.getFields().size() + "\n";
						}
						else
							text = "Cannot display metrics due to syntax error in this file.";
						
						metricsList.setText(text);

						//Event for viewing graphs
						EventHandler<ActionEvent> showGraphs = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								showScreen(5);
								Node pie = graph.generateMethodsChart(metric);
								root.getChildren().add(pie);
								Node bar = graph.generateFieldsChart(metric);
								root.getChildren().add(bar);
							}
						};
						
						EventHandler<ActionEvent> showFields = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String text = "";
								for (FieldMetrics f : metric.getFields()) {
									text += f.getFieldName() + "\n";
									text += "Type:			" + f.getType() + "\n";
									text += "Times used: 		" + f.getUseCount() + "\n";
									text += "\n";
								}
								
								if (text.length() > 0)
									metricsList.setText(text);
								else
									metricsList.setText("This file does not have any fields.");
							}
						};
						
						EventHandler<ActionEvent> showMethods = new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String text = "";
								
								for (MethodMetrics m : metric.getMethods()) {
									text += m.getMethodName() + "\n";
									text += "Return type:		" + m.getReturnType() + "\n";
									text += "Lines:			" + m.getNumOfLines() + "\n";
									text += "Arguments:		";
									
									String args = "";
									for (int i = 0; i < m.getArguments().length; i++)
										args += m.getArguments()[i] + ", ";
									
									if (!args.equals(", "))
										text += args + "\n";
									else
										text += "NONE \n";
									
									text += "\n \n";
								}
								
								if (text.length() > 0)
									metricsList.setText(text);
								else
									metricsList.setText("This file does not have any methods.");
							}
						};
						
						visualiseButton.setOnAction(showGraphs);
						fieldsButton.setOnAction(showFields);
						methodsButton.setOnAction(showMethods);
					}
				};

				button.setOnAction(metricsEvent);
				root.getChildren().add(button);
				n++;
			}
			
			EventHandler<ActionEvent> nextEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (metricsPage < (reader.getAllFiles().size() / 17)) {
						metricsPage++;
						viewMetricsEvent.handle(null);
					}
				}};

			EventHandler<ActionEvent> prevEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (metricsPage > 0) {
						metricsPage--;
						viewMetricsEvent.handle(null);
					}
				}};
				
				Button prev = new Button("<-");
				prev.setLayoutY((17 * 30) + 85);
				prev.setLayoutX(25);
				prev.setOnAction(prevEvent);
				root.getChildren().add(prev);
				
				Button next = new Button("->");
				next.setLayoutY((17 * 30) + 85);
				next.setLayoutX(230);
				next.setOnAction(nextEvent);
				root.getChildren().add(next);
			}
		};
		
		EventHandler<ActionEvent> viewSmellsEvent = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			showScreen(4);
			smellNodes.clear();
			String selectedSmellType = smellDropDown.getValue().toLowerCase();
			
			for (int i = 0; i < reader.getAllSmells().size(); i++) {
				CodeSmells smell = reader.getAllSmells().get(i);
				String smellType = smell.getSmellType().toLowerCase();
				
				//Continues if it is not the right category of smell
				if (!selectedSmellType.equals("all") && !smellType.equalsIgnoreCase(selectedSmellType))
					continue;
				
				Text smellName = new Text(smell.getSmellName());
				smellName.setLayoutX(60);
				smellName.setScaleX(1.5f);
				smellName.setScaleY(1.5f);
				
				TextArea smellDesc = new TextArea(smell.getSmellDesc());
				Background descBack = null;
				
				if (smell.getSmellType() == "Errors")
					descBack = new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY));
				else
					descBack = new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
				
				smellDesc.setLayoutX(50);
				smellDesc.setPrefSize(800, 125);
				smellDesc.setBackground(descBack);
				smellDesc.setWrapText(true);
				smellDesc.setEditable(false);
				smellDesc.setBorder(null);
				Font smellFont = smellDesc.getFont();
				float fontSize = (float)smellFont.getSize() + 5.0f;
				smellDesc.setFont(Font.font(fontSize));

				smellNodes.add(new CodeSmellNode(smellName, smellDesc));
			}
			
			smellPage = 0;
			renderSmell(smellPage);
		}
	};
	
	public static void main(String[] args) throws IOException {
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
		descLabel.setLayoutY(420);
		
		uploadButton.setPrefSize(canvas.getWidth() - 100, 100);
		uploadButton.setLayoutX(50);
		uploadButton.setLayoutY(100);
		uploadButton.setOnAction(uploadEvent);
		Font uploadFont = uploadButton.getFont();
		float uploadFSize = (float)uploadFont.getSize() + 10.0f;
		uploadButton.setFont(Font.font(uploadFSize));
		
		uploadRecentButton.setPrefSize(canvas.getWidth() - 100, 100);
		uploadRecentButton.setLayoutX(50);
		uploadRecentButton.setLayoutY(210);
		uploadRecentButton.setOnAction(openRecentEvent);
		Font uploadRFont = uploadRecentButton.getFont();
		float uploadRFSize = (float)uploadRFont.getSize() + 10.0f;
		uploadRecentButton.setFont(Font.font(uploadRFSize));

		gitHubLabel.setLayoutX(150);
		gitHubLabel.setLayoutY(335);
		gitHubLabel.setScaleX(2.5);
		gitHubLabel.setScaleY(2.5);

		githubUserBox.setPromptText("username");
		githubUserBox.setLayoutX(235);
		githubUserBox.setLayoutY(320);
		githubUserBox.setPrefSize(canvas.getWidth() - 1000, 40);
		Font githubUFont = githubUserBox.getFont();
		float gitUFSize = (float)githubUFont.getSize() + 10.0f;
		githubUserBox.setFont(Font.font(gitUFSize));

		githubRepoBox.setPromptText("repository");
		githubRepoBox.setLayoutX(535);
		githubRepoBox.setLayoutY(320);
		githubRepoBox.setPrefSize(canvas.getWidth() - 1000, 40);
		Font githubRFont = githubRepoBox.getFont();
		float gitRFSize = (float)githubRFont.getSize() + 10.0f;
		githubRepoBox.setFont(Font.font(gitRFSize));

		githubConnect.setLayoutX(850);
		githubConnect.setLayoutY(340);
		githubConnect.setScaleX(1.5);
		githubConnect.setScaleY(1.5);
		githubConnect.setOnAction(githubEvent);
		
		quitButton.setScaleX(1.5);
		quitButton.setScaleY(1.5);
		quitButton.setLayoutX(25);
		quitButton.setLayoutY(650);
		quitButton.setOnAction(quitEvent);

		//Objects for selection screen
		overviewButton.setPrefSize((canvas.getWidth() - 100) / 4, 50);
		overviewButton.setLayoutX(42);
		overviewButton.setLayoutY(20);
		overviewButton.setOnAction(viewSelectionEvent);
		
		codeButton.setPrefSize((canvas.getWidth() - 100) / 4, 50);
		codeButton.setLayoutX(342);
		codeButton.setLayoutY(20);
		codeButton.setOnAction(viewSourceEvent);

		metricsButton.setPrefSize((canvas.getWidth() - 100) / 4, 50);
		metricsButton.setLayoutX(642);
		metricsButton.setLayoutY(20);
		metricsButton.setOnAction(viewMetricsEvent);
		
		smellsButton.setPrefSize((canvas.getWidth() - 100) / 4, 50);
		smellsButton.setLayoutX(942);
		smellsButton.setLayoutY(20);
		smellsButton.setOnAction(viewSmellsEvent);

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

		codeOverview.setLayoutX(42);
		codeOverview.setLayoutY(80);
		codeOverview.setPrefSize(500, 550);
		codeOverview.setEditable(false);
		Font overviewFont = codeOverview.getFont();
		float overviewFSize = (float)overviewFont.getSize() + 10.0f;
		codeOverview.setFont(Font.font(overviewFSize));
		
		//Source code screen
		code.setLayoutX(265);
		code.setLayoutY(80);
		code.setPrefSize(1000, 600);
		code.setEditable(false);
		Font codeFont = code.getFont();
		float codeFSize = (float)codeFont.getSize() + 3.0f;
		code.setFont(Font.font(codeFSize));
		
		//Metrics screen
		metricsList.setLayoutX(265);
		metricsList.setLayoutY(80);
		metricsList.setPrefSize(1000, 550);
		metricsList.setEditable(false);
		Font metricsFont = metricsList.getFont();
		float metricsFSize = (float)metricsFont.getSize() + 10.0f;
		metricsList.setFont(Font.font(metricsFSize));

		visualiseButton.setScaleX(1.5);
		visualiseButton.setScaleY(1.5);
		visualiseButton.setLayoutX(290);
		visualiseButton.setLayoutY(650);

		fieldsButton.setScaleX(1.5);
		fieldsButton.setScaleY(1.5);
		fieldsButton.setLayoutX(500);
		fieldsButton.setLayoutY(650);

		methodsButton.setScaleX(1.5);
		methodsButton.setScaleY(1.5);
		methodsButton.setLayoutX(690);
		methodsButton.setLayoutY(650);
		
		//Smells
		ArrayList<String> typeData = new ArrayList<String>();
		typeData.add("All");
		typeData.add("Errors");
		typeData.add("Bloaters");
		typeData.add("Object Orient Abusers");
		typeData.add("Change Preventers");
		typeData.add("Dispensables");
		typeData.add("Couplers");
		smellDropDown.setItems(FXCollections.observableList(typeData));
		smellDropDown.setValue(smellDropDown.getItems().get(0));
		smellDropDown.setScaleX(1.5f);
		smellDropDown.setScaleY(1.5f);
		smellDropDown.setLayoutX(1033);
		smellDropDown.setLayoutY(100);

		noSmellsLabel.setScaleX(1.5f);
		noSmellsLabel.setScaleY(1.5f);
		noSmellsLabel.setLayoutX(400);
		noSmellsLabel.setLayoutY(150);
		noSmellsLabel.setVisible(false);

		refresh.setScaleX(1.5);
		refresh.setScaleY(1.5);
		refresh.setLayoutX(1008);
		refresh.setLayoutY(150);
		refresh.setOnAction(viewSmellsEvent);

		smellPrevPage.setLayoutX(50);
		smellPrevPage.setLayoutY(575);
		smellPrevPage.setScaleX(1.5f);
		smellPrevPage.setScaleY(1.5f);
		smellPrevPage.setOnAction(prevSmellPageEvent);
		
		smellNextPage.setLayoutX(815);
		smellNextPage.setLayoutY(575);
		smellNextPage.setScaleX(1.5f);
		smellNextPage.setScaleY(1.5f);
		smellNextPage.setOnAction(nextSmellPageEvent);

		pageNum.setScaleX(1.5f);
		pageNum.setScaleY(1.5f);
		pageNum.setLayoutX(425);
		pageNum.setLayoutY(575);
		
		//Add elements to scene
		root.getChildren().add(canvas);

		mainScreen.add(tipLabel);
		mainScreen.add(descLabel);
		mainScreen.add(uploadButton);
		mainScreen.add(githubUserBox);
		mainScreen.add(githubRepoBox);
		mainScreen.add(gitHubLabel);
		mainScreen.add(githubConnect);
		mainScreen.add(uploadRecentButton);
		mainScreen.add(quitButton);

		selectionScreen.add(codeButton);
		selectionScreen.add(metricsButton);
		selectionScreen.add(smellsButton);
		selectionScreen.add(overviewButton);
		selectionScreen.add(backButton);
		selectionScreen.add(codeOverview);

		sourceCodeScreen.add(codeButton);
		sourceCodeScreen.add(metricsButton);
		sourceCodeScreen.add(smellsButton);
		sourceCodeScreen.add(overviewButton);
		sourceCodeScreen.add(backButton);
		sourceCodeScreen.add(code);
		
		metricsScreen.add(codeButton);
		metricsScreen.add(metricsButton);
		metricsScreen.add(smellsButton);
		metricsScreen.add(overviewButton);
		metricsScreen.add(backButton);
		metricsScreen.add(metricsList);
		metricsScreen.add(visualiseButton);
		metricsScreen.add(methodsButton);
		metricsScreen.add(fieldsButton);
		
		visualisedScreen.add(toMetricsButton);

		smellsScreen.add((Node) smellDropDown);
		smellsScreen.add(refresh);
		smellsScreen.add(overviewButton);
		smellsScreen.add(codeButton);
		smellsScreen.add(metricsButton);
		smellsScreen.add(smellsButton);
		smellsScreen.add(backButton);
		smellsScreen.add(smellNextPage);
		smellsScreen.add(smellPrevPage);
		smellsScreen.add(pageNum);
		smellsScreen.add(noSmellsLabel);
		
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
	 * Uploads a class/workspace
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void upload(File file) {		
		//Exits method if user doesn't select file
		if (file == null)
			return;
		
		int i = file.getName().lastIndexOf(".");
		String ext = "directory";
		
		//Directories will return -1 since there's no dots
		if (i >= 0)
			ext = file.getName().substring(i + 1);
		
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
		
		// to next screen if it finds java files
		if (reader.getAllFiles().size() > 0) {
			saveToRecent(file.getPath());
			renderOverview();
		}
		else
			showMessage("No java files found.");
	}
	
	/**
	 * 
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void renderOverview() {
		reader.readAllFiles();
		ArrayList<Metrics> metrics = reader.getAllMetrics();
		String text = "This project contains " + reader.getAllSmells().size() + " code smell(s).\n\n";
		text +=  "This project is made up of " + metrics.size() + " file(s).\n";
		int totalLines = 0;
		String list = "";

		for (int i = 0; i < metrics.size(); i++) {
			Metrics m = metrics.get(i);
			list += m.getFileName() + ": " + m.getTotalLines() + " lines \n";
			totalLines += m.getTotalLines();
		}
		
		text +=  "Totalling in " + totalLines + " line(s).\n\n";
		text += list;
		
		while (selectionScreen.size() >= 7)
			selectionScreen.remove(selectionScreen.get(selectionScreen.size() - 1));
		
		codeOverview.setText(text);
		selectionScreen.add(graph.generateOverviewChart(reader.getAllMetrics()));
		showScreen(1);
	}
	
	/**
	 * Opens the most recent file/project from the recent
	 * directories text file
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void uploadFromRecent() {
		try {
			File file = new File("recent_directories.txt");
			Scanner scanner = new Scanner(file);
			String path = scanner.nextLine();
			
			if (path.length() > 0)
				upload(new File(path));
				
			scanner.close();
		}
		catch(NoSuchElementException e) {
			showMessage("'recent_directories.txt' is empty.");
			System.out.println(e);
		}
		catch (Exception e) {
			showMessage("Cannot find 'recent_directories.txt' file.");
			System.out.println(e);
		}
	}
	
	/**
	 * Writes current directory opened as most recent
	 * and adds it to text file so it can be read in the future
	 * 
	 * @param directory
	 * @return nothing
	 */
	private void saveToRecent(String directory) {
		try {
			File file = new File("recent_directories.txt");
			PrintWriter writer = new PrintWriter(file);
			writer.write(directory);
			writer.close();
		}
		catch(Exception e) {
			showMessage("Cannot save directory to 'recent_directories.txt'.");
			System.out.println(e);
		}
	}
	
	/**
	 * Renders all the code smells
	 * 
	 * @param page int
	 * @return nothing
	 */
	private void renderSmell(int page) {
		showScreen(4);
		pageNum.setText((page + 1) + "");
		int n = 0;
		if (smellNodes.size() == 0)
			noSmellsLabel.setVisible(true);
		
		//Renders next 3 smells in list
		for (int i = (page * 3); i <= (page * 3 + 2); i++) {
			if (i >= smellNodes.size())
				return;
			
			Node title = smellNodes.get(i).getTitle();
			Node desc = smellNodes.get(i).getDesc();
			title.setLayoutY((n * 160) + 90);
			desc.setLayoutY((n * 160) + 98);
			n++;
			
			if (!root.getChildren().contains(title))
				root.getChildren().add(title);
			
			if (!root.getChildren().contains(desc))
				root.getChildren().add(desc);
		}
	}
	
	/*
	 * Accesses the github repository the user typed
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void connectGithub() throws IOException {
		String user = githubUserBox.getText();
		String repo = githubRepoBox.getText();
		String url = "https://www.github.com/" + user + "/" + repo + "/";
		reader.addFromGithub(url);
		renderOverview();
		showScreen(1);
	}
	
	/**
	 * Displays message to the user
	 * 
	 * @param message
	 * @return nothing
	 */
	void showMessage(String message) {
		JOptionPane alert = new JOptionPane();
		JOptionPane.showMessageDialog(alert, message);
	}
}