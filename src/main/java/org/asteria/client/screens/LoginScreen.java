package org.asteria.client.screens;

import org.asteria.CommonSettings;
import org.asteria.client.services.SpeechService;
import org.flowutils.time.RealTime;
import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.messageduct.account.messages.AccountErrorMessage;
import org.messageduct.account.messages.CreateAccountSuccessMessage;
import org.messageduct.account.messages.LoginSuccessMessage;
import org.messageduct.client.ClientNetworking;
import org.messageduct.client.ServerListenerAdapter;

import static org.flowutils.Check.notNull;

/**
 * Screen for logging into a game server
 */
public final class LoginScreen extends ScreenBase {

    private final ClientNetworking clientNetworking;
    private SpeechService speechService;

    private final ServerListenerAdapter serverListener = new ServerListenerAdapter() {
        @Override public void onConnected(ClientNetworking clientNetworking) {
            speechService.say("Connected to server!");
        }

        @Override
        public void onLoggedIn(ClientNetworking clientNetworking, LoginSuccessMessage loginSuccessMessage) {
            speechService.say("Logged in to server!");
        }

        @Override public void onAccountCreated(ClientNetworking clientNetworking,
                                               CreateAccountSuccessMessage createAccountSuccessMessage) {
            speechService.say("Account created!");
        }

        @Override
        public void onAccountErrorMessage(ClientNetworking clientNetworking, AccountErrorMessage accountErrorMessage) {
            speechService.say("Account problem: " + accountErrorMessage.getErrorMessage());
        }

        @Override public void onException(ClientNetworking clientNetworking, Throwable e) {
            speechService.say("Error: " + e.getMessage());
        }
    };

    public LoginScreen(ClientNetworking clientNetworking, SpeechService speechService) {
        notNull(clientNetworking, "clientNetworking");
        notNull(speechService, "speechService");
        this.speechService = speechService;
        this.clientNetworking = clientNetworking;

        clientNetworking.addListener(serverListener);
    }

    @Override protected void onCreate(Skin skin, TextureAtlas textureAtlas, Stage stage, Table rootUi) {

        speechService.say("Welcome to Asteria.");

        // Create UI widgets for entering username and password.
        // See https://github.com/libgdx/libgdx/wiki/Scene2d.ui for more documentation
    	
        // Username field
        final TextField usernameField = new TextField("", skin);
        usernameField.selectAll();
        stage.setKeyboardFocus(usernameField);

        // Password field
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        
        // Register button
        final TextButton registerButton = new TextButton("Register a new player", skin);
        registerButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
            	//swapToRegister();

                // DEBUG: Quick hack to register using the username and password typed into the login fields
                registerUser(usernameField.getText(), passwordField.getText(), null);
            }
        });

        // Login button
        final TextButton loginButton = new TextButton("Login", skin);
        loginButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                speechService.say("Logging in...");
                loginToServer(usernameField.getText(), passwordField.getText());
            }
        });

        // Also activate on enter press
        InputListener enterListener = new InputListener() {
            @Override public boolean keyTyped(InputEvent event, char character) {
                if (event.getKeyCode() == Input.Keys.ENTER) {
                    return loginToServer(usernameField.getText(), passwordField.getText());
                } else {
                    if (Character.isLetterOrDigit(event.getCharacter()))  {
                        speechService.say(usernameField.getText());
                    }
                    return false;
                }

            }
        };
        usernameField.addListener(enterListener);
        passwordField.addListener(enterListener);

        // Add the UI components together and layout the UI
        // See https://github.com/libgdx/libgdx/wiki/Table for documentation on laying out UI components with Table.
        rootUi.right().bottom();
        rootUi.defaults().pad(GAP);
        
        rootUi.add(new Label("Username", skin));
        rootUi.add(usernameField);
        rootUi.add(new Label("Password", skin));
        rootUi.add(passwordField);
        rootUi.add(loginButton).width(120);
        rootUi.row();
        rootUi.add(registerButton).width(240).height(20).colspan(5).right();

    }
    
    @Override
    protected void onRender(RealTime t) {
    	
    	GL11.glRotatef((float) (Math.sin(t.getStepCount()*0.00001f)*100f), 0, 0, 1);
    	
    	GL11.glBegin(GL11.GL_LINES);
    	for(int i=0; i < 10; i++){
    		GL11.glColor3f(1, 0, 0);
    		GL11.glVertex3f(-1, -i/10.0f, 0);
    		GL11.glVertex3f(1, i/10.0f, 0);
    	}
    	GL11.glEnd();
    	
    };

    private boolean loginToServer(String username, String password) {
        System.out.println("Logging in with username '" + username + "' and pass '" + password + "'");

        // LATER: Get server info from a list of stored servers, or a new one entered by the user
        clientNetworking.connect(CommonSettings.NETWORK_CONFIG, CommonSettings.SERVER_INFO);
        clientNetworking.login(username, password.toCharArray());

		return true;
    }


    private boolean registerUser(String username, String password, String email) {
        System.out.println("Registering '" + username + "' pass: '" + password + "'" + "' email: '" + email + "'");

        // LATER: Connect should happen earlier than register or login
        clientNetworking.connect(CommonSettings.NETWORK_CONFIG, CommonSettings.SERVER_INFO);
        clientNetworking.createAccount(username, password.toCharArray());

        return true;
    }


}
