package com.example.carleds;

//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class CarLeds extends Activity implements OnClickListener {
	Button btn_up, btn_left, btn_right, btn_stop,btn_down,btn_connect;
	
	public static final String TAG ="Carleds";
	public static final boolean D=true;
	//Tipos de mensajes enviados y recibidos dsde el handler de conexion
	public static final int Mensaje_Estado_Cambiado =1;
	public static final int Mensaje_Leido=2;
	public static final int Mensaje_Escrito=3;
	public static final int Mensaje_Nombre_Dispositivo=4;
	public static final int Mensaje_TOAST=5;
	public static final int MESSAGE_Desconectado=6;
	public static final int REQUEST_ENABLE_TB=7;
	public static String DEVICE_NAME="device_name";
	public static final String TOAST = "toast";
	
	public static final String address="20:13:08:09:05:27";
	//nombre del dispositivo
	private String mConnectedDeviceName=null;
	//adaptador bluetooth local
	private BluetoothAdapter AdaptadorBT=null;
	//objeto miembro para el servicio de ConexionBT
	private ConexionBT Servicio_BT = null;
	//vibrador
	private Vibrator vibrador;
	//variable para el menu de opciones
	private boolean seleccionador = false;
	public int Opcion=R.menu.car_leds;
	
	public int stateConnect=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_leds);
		btn_up=(Button) findViewById(R.id.btn_up);
		btn_left=(Button) findViewById(R.id.btn_left);
		btn_right=(Button) findViewById(R.id.btn_right);
		btn_stop=(Button) findViewById(R.id.btn_stop);
		btn_down=(Button) findViewById(R.id.btn_down);
		btn_connect=(Button) findViewById(R.id.btn_connect);
		
		btn_up.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_down.setOnClickListener(this);
		btn_connect.setOnClickListener(this);
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_leds, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btn_up:
			if(D) Log.e("Encendido","adelante");
			sendMessage("0");
			break;
		case R.id.btn_left:
			if(D) Log.e("Encendido","derecha");
			sendMessage("1");
			break;
		case R.id.btn_right:
			if(D) Log.e("Encendido","izquierda");
			sendMessage("2");
			break;
		case R.id.btn_stop:
			if(D) Log.e("Encendido","stop");
			sendMessage("3");
			break;
		case R.id.btn_down:
			if(D) Log.e("Encendido","abajo");
			sendMessage("4");
			break;
		case R.id.btn_connect:
			sendMessage("5");
			connect();
			
			break;
		
		}
		
	}

	private void sendMessage(String message) {
		// TODO Auto-generated method stub
		if(Servicio_BT.getState()==ConexionBT.STATE_CONNECTED){
			if(message.length()>0){
				byte[] send = message.getBytes(); //obtenemos los bytes del mensaje
				if(D) Log.e(TAG,"Mensaje enviado: "+message);
				Servicio_BT.write(send);
				}
			}else{
				if(message !="5")
					Toast.makeText(this,"No conectado",Toast.LENGTH_SHORT).show();
				}
	}// fin del mensaje


	public void onStart(){
		super.onStart();
		ConfigBT();
	}
	public void onDestroy(){
		super.onDestroy();
		if(Servicio_BT!=null)
			Servicio_BT.stop(); //desconectamos el servicio
	}
	public void ConfigBT() {
		// TODO Auto-generated method stub
		//obtenemos el adaptador bluetooth
		AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
		if(AdaptadorBT.isEnabled()){ // si el BT esta encendido
			if(Servicio_BT == null){ // y el servicio es nulo, invocamos el Servicio_BT
				Servicio_BT= new ConexionBT(this,mHandler);
			}
		}else{
			if(D) Log.e("Setup","Bluetooth apagado..");
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth,REQUEST_ENABLE_TB);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// una vez que se ha realizado una actividad regresa un resulado
		switch(requestCode){
		case REQUEST_ENABLE_TB: // respuesta de intento de encendido
			if(resultCode == Activity.RESULT_OK){ // BT esta acitvado e iniciamos servicios
				ConfigBT();
			}else{
				finish();
			}
			break;
		}
	}

	private void connect() {
		// TODO Auto-generated method stub
		if(stateConnect==0){
			if(D) Log.e("Conexion","Conectando");
			vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrador.vibrate(500);
			BluetoothDevice device=AdaptadorBT.getRemoteDevice(address);
			Servicio_BT.connect(device);
			btn_connect.setText("Desconectar");
			
		
			stateConnect=1;
		}else{
			if(Servicio_BT != null) 
				Servicio_BT.stop();	
			
			btn_connect.setText("Conectar");
			Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();
		
			stateConnect =0;
		}
		
		return;
	}
	
	final Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
				switch(msg.what){
					case Mensaje_Escrito:
						byte[] writeBuf = (byte[]) msg.obj;
						String writeMessage = new String(writeBuf);
						if(D) Log.e(TAG,"Message_write =w= "+writeMessage);
					break;
					
					case Mensaje_Leido:
						byte[] readBuf = (byte[]) msg.obj;
						//Construye un Sting de los bytes validos en el buffer
						String readMessage = new String(readBuf, 0, msg.arg1);
						if(D) Log.e(TAG, "Message_read =w= "+readMessage);
						break;
					case Mensaje_Nombre_Dispositivo:
						mConnectedDeviceName=msg.getData().getString(DEVICE_NAME);
						Toast.makeText(getApplicationContext(), "Conectado con "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
						seleccionador= true;
						break;
					case Mensaje_TOAST:
						Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
						break;
					case MESSAGE_Desconectado:
						if(D) Log.e("Conexion","Desconectados");
						seleccionador = false;
						break;
				}
		}
	};



}














