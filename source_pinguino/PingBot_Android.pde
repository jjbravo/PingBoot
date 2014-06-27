char* strin="Bienvenido\n";
int a=0;
u8 dato=0;
void stop();
void setup(){
 pinMode(0,OUTPUT);
	pinMode(1,OUTPUT);
	pinMode(2,OUTPUT);
	pinMode(3,OUTPUT);
	pinMode(4,OUTPUT);
	pinMode(5,OUTPUT);
	
	pinMode(6,OUTPUT); //LED INDICADOR
	pinMode(7,OUTPUT); //LED INDICADOR
	Serial.begin(9600);
	//Serial.printf(strin);
	stop();
	
}
void adelante(){
//stop();
	digitalWrite(0,HIGH);
	digitalWrite(2,HIGH);
	//digitalWrite(4,HIGH);//LED INDICADORES
	//digitalWrite(5,HIGH);//LED INDICADORES
	
	digitalWrite(7,HIGH);//LED INDICADOR
	delay(10);
	

}
void atras(){
//stop();

	digitalWrite(1,HIGH);
	digitalWrite(3,HIGH);
	digitalWrite(6,HIGH);//LED INDICADOR
	delay(10);

}

void stop(){
	digitalWrite(0,LOW);
	digitalWrite(1,LOW);
	digitalWrite(2,LOW);
	digitalWrite(3,LOW);
	digitalWrite(4,LOW);
	digitalWrite(5,LOW);
	
	digitalWrite(6,LOW);//LED INDICADOR
	digitalWrite(7,LOW);//LED INDICADOR
	delay(10);
	
}
void izq(){
//stop();

	digitalWrite(0,HIGH);
	digitalWrite(4,HIGH);// LED INDICADOR
	
	digitalWrite(3,HIGH);//****************
	delay(10);
	}
void dere(){
//stop();

	digitalWrite(1,HIGH);//*******************
	
	digitalWrite(2,HIGH);
	digitalWrite(5,HIGH);//LED INDICADOR
	delay(10);
}


void loop(){
	
while(Serial.available()){
 dato=Serial.read();

	
		 switch(dato){
			case 25:
				stop();
				atras();
				delay(300);
				break;
			case 6:
				stop();
				adelante();
				delay(300);
				break;
			case 103:
				stop();
				izq();
				delay(300);
				break;
			case 51:
				stop();
				dere();
				delay(300);
				break;	
			case 102:
				stop();
				
				break;
			case 101: //5 para desconectar y apagar todo
				stop();
				
				break;	

		 }
	
 }
 
}