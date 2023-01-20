package com.example.emu8086;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button b1,b2;
    EditText et1;
    TextView tv1, tv2; //tv2 for output
    HashMap<String,Integer> RTI  = new  HashMap<String,Integer>();
    String RegNames[] = new String[10];//{"AX","BX"};
    int RegVal[] = new int[10];
    int NumReg = 0;
    String Inst[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        et1 = findViewById(R.id.et1);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);

        regToIndex();
        initReg();
        tv2.setText(printReg());
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sprg = et1.getText().toString();
                //String text="";
                if(sprg.length() > 0)
                {
                    String prg[] = sprg.split("\\n");
                    for(int i=0; i<prg.length; i++)
                    {
                        if(prg[i].length() > 0) {
                            //text = text +"line"+i+":"+prg[i];
                            instSep(prg[i].trim());
                            processInst();
                            //tv2.setText("Output:"+printReg());
                        }
                    }//end for
                }//end if
                //tv2.setText(text);
            }//endonclick
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv2.setText(printReg());
            }
        });

    }//end oncreate
    public void instSep(String inst)
    {
        //String inst ="MOV AX, BX";
        //System.out.println(inst);
        Log.i("IninstSepMethod",inst);
        inst = inst.toUpperCase().trim().toUpperCase();
        inst = inst.replace(",", "");
        //System.out.println(inst);
        Inst = inst.split(" ");
        /*for(int i=0; i<Inst.length;i++)
        {
            System.out.println(i+" "+Inst[i]);

        }*/
    }//end method
    public boolean processInst()
    {
        int sindex =-1, dindex=-1;
        int svalue=-1, dvalue=-1;
        Inst[0] = Inst[0].trim();
        Inst[1] = Inst[1].trim();
        Inst[2] = Inst[2].trim();
        int stype = getOperandType(Inst[2]);
        int dtype = getOperandType(Inst[1]);
        System.out.println(Inst[2]+":"+stype);
        System.out.println(Inst[1]+":"+dtype);
        if(dtype == 2 || (stype == -1 && dtype ==-1))
        {
            //System.out.println("Invaild operands...!");
            return false;
        }
        else if(stype ==3 && dtype ==3)
        {
            //System.out.println("Invaild operands...!");
            return false;
        }
        else if(stype ==3 || dtype ==3)
        {
            //System.out.println("Either source or destination operand is memory");
            if(stype ==3)
            {
                String s = Inst[2].substring(1,Inst[2].indexOf("]"));
                if(!RTI.containsKey(Inst[2]))
                {  //System.out.println("Operand Problem....Memory is not initialized");
                }


            }
            if(dtype ==3)
            {
                String s = Inst[1].substring(1,Inst[1].indexOf("]"));
                if(!RTI.containsKey(Inst[1]))
                {  RTI.put(Inst[1], NumReg); RegNames[NumReg] = Inst[1];
                    //dindex = NumReg;
                    NumReg++;
                }


            }
        }//
        else{}
        dindex = RTI.get(Inst[1]);
        //System.out.println("Dindex:"+dindex);
        switch(Inst[0])
        {
            case "MOV":
                if(stype == 2)
                {
                    String s = Inst[2].substring(0,Inst[2].indexOf("H"));
                    RegVal[dindex]= getDecimal(s);
                }
                else
                {
                    sindex = RTI.get(Inst[2]);
                    //System.out.println("Sindex:"+sindex);
                    RegVal[dindex] = RegVal[sindex];
                }
                break;
            case "ADD":
                if(stype == 2)
                {
                    String s = Inst[2].substring(0,Inst[2].indexOf("H"));
                    RegVal[dindex]+= getDecimal(s);
                }
                else
                {
                    sindex = RTI.get(Inst[2]);
                    //System.out.println("Sindex:"+sindex);
                    RegVal[dindex] += RegVal[sindex];
                }

                break;
            case "SUB":
                if(stype == 2)
                {
                    String s = Inst[2].substring(0,Inst[2].indexOf("H"));

                    RegVal[dindex]-= getDecimal(s);
                }
                else
                {
                    sindex = RTI.get(Inst[2]);
                    //System.out.println("Sindex:"+sindex);
                    RegVal[dindex] -= RegVal[sindex];
                }

                break;
            default: Log.i("Error","Instruction is not matched");//System.out.println("Instruction is not matched");

        }

        return true;
    }//end method
    public int getRegType(String s)
    {
        int type =-1; // 1 for Reg and 2 Imediate 3 for Memory
        switch(s)
        {
            case "AX": type =0; break;
            case "BX": type =1; break;
            case "CX": type =2; break;
            case "DX": type =3; break;

            default:
                type =-1;

        }//end switch
        return type;
    }
    public int getOperandType(String s)
    {
        int type =-1; // 1 for Reg and 2 Imediate 3 for Memory
        switch(s)
        {
            case "AX":
            case "BX":
            case "CX":
            case "DX":
                type =1;
                break;
            default:
                type =-1;

        }//end switch

        if(type == -1)//not regester then Immediate or memory
        {
            //for immediate
            if(s.contains("[") && s.contains("]"))
            {
                type =3; //memory
            }
            else if(s.endsWith("H"))
                type = 2; //direct addressing
            else
                type = -1;

        }

        return type;
    }//end method
    public int processReg(String r, int type)
    {
        int value =-1;
        if(type==1)//get value from reg
        {
            int index = RTI.get(r);
            value = RegVal[index];
        }
        else if(type ==2)
        {
            String s = r.substring(0,r.indexOf("H"));
            try{
                //value = Integer.parseInt(s);
                value = getDecimal(s);
            }catch(Exception e){System.out.println(""+e);}
        }
        else
        {
            //System.out.println("[BX]".substring(1,"[BX]".indexOf("]")));
            String s = r.substring(1,r.indexOf("]"));
            int t = getRegType(s);
            if(t ==-1)
            {
                if(!RTI.containsKey(r))
                    //RTI.put(r, NumReg); RegNames[NumReg] = r;
                    System.out.println("Problem!!!");
                else
                {
                    int index = RTI.get(r);
                    value = RegVal[index];
                }

            }
            else
            {  int index = RTI.get(r);
                int dec_value = RegVal[index];
                String str = "["+Integer.toHexString(dec_value)+"]";
                if(!RTI.containsKey(r))
                    //RTI.put(r, NumReg); RegNames[NumReg] = r;
                    //System.out.println("Problem!!!");
                    Log.i("OperandProblem","Problem!!!");
                else
                {
                    index = -1;
                    index = RTI.get(str);
                    value = RegVal[index];
                }
            }
        }
        return value;
    }//end method


    public void regToIndex()
    {
        RTI.put("AX", 0); RegNames[NumReg++] = "AX";
        RTI.put("BX", 1); RegNames[NumReg++] = "BX";
        RTI.put("CX", 2); RegNames[NumReg++] = "CX";
        RTI.put("DX", 3); RegNames[NumReg++] = "DX";

    }//end method

    public  int getDecimal(String hex)
    {
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }//end method
    public void initReg()
    {
        //System.out.println("Registers are Initialized.....");
        for(int i=0;i<NumReg; i++)
            RegVal[i]= 0;
        //System.out.println("**********************************");
    }//end method
    public String printReg()
    {   String text ="";
        //System.out.println("**********************************");
        //System.out.println("Register Contents");
        for(int i=0;i<NumReg; i++)
            //System.out.println(RegNames[i]+":"+RegVal[i]);
            text = text + RegNames[i]+":"+RegVal[i]+"  ";
        //System.out.println("**********************************");
        return text;
    }//end method
}//end class
