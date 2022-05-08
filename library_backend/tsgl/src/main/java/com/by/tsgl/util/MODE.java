package com.by.tsgl.util;

public enum MODE {

            ALL(0),NAME(1),AUTHOR(2),PRESS(3),
            ISBN(4),KEYWORD(5),CALL_NUMBER(6),    //用于模糊匹配的匹配模式

            WAIT_FOR_PAY(0),PAID(1),REFUND(-1),     //付款状态

            S_DEPOSIT("缴纳押金"),S_FINE("缴纳罚金"),S_RECHARGE("余额充值"),

            LEAVE_MESSAGE(1),REPLY_MESSAGE(2),

            IN_THE_MUSEUM("在馆"),LEND("借出"),LOSS("遗失"),
            TS_LOSS("图书丢失"),TS_LATE("图书逾期"),TS_DAMAGE("图书损坏"),

            //借阅证等级
            READER_GRADE_1("1"),READER_GRADE_2("2"),READER_GRADE_3("3"),
            READER_GRADE_4("4"),READER_GRADE_5("5");

            private int val;
            private String str;
            MODE(int value){
                this.val=value;
            }

            MODE(String str){this.str=str;}
            public int getVal(){
                return val;
            }
            public String getStr(){return str;}
}
