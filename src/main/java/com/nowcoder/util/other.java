package com.nowcoder.util;

/**
 * Created by mffh on 2019/8/4
 */
public class other {
    public static void main(String[]args){
        String i= "123.456e+6";
        char[] x=i.toCharArray();
        boolean q=new other().isNumeric(x);
        if(q)
        System.out.println("true");
        if(!q)
            System.out.println("false");
    }

        public boolean isNumeric(char[] str) {
            boolean hasE=false,mark=false,hasdot=false;
            for(int i=0;i<str.length;i++){
                if(str[i]=='e'||str[i]=='E'){
                    if(hasE){
                        return false;}
                    if((str[i+1]<'0'||str[i+1]>'9')&&(str[i+1]!='+'||str[i+1]!='-')){
                        System.out.println(str[i]+" 1"+" "+str[i+1]);
                    return false;}
                    hasE=true;
                }
                else if(str[i]=='+'||str[i]=='-'){
                    if(mark==false&&i!=0&&(str[i-1]!='e'||str[i-1]!='E')){
                        System.out.println(str[i]+" 2");
                        return false;}
                    if(mark&&(str[i-1]!='e'||str[i-1]!='E')){
                        System.out.println(str[i]+" 3");
                        return false;}
                    mark=true;
                }else if(str[i]=='.'){
                    if(hasdot||hasE){
                        return false;}
                    hasdot=true;
                }else if(str[i]<'0'||str[i]>'9'){
                    return false;
                }
            }
            return true;
        }
    }



