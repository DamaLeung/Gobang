package com.example.gobang;

public class Chess {

    int defense_tag;	//防守危险度
    int offense_tag;	//进攻优先度
    int occupy;		//占有位
    int defense_point;	//防守分数
    int offense_point;	//进攻分数
    int total_point;    //总分数
    int fin;          //出现胜负威胁个数
    int huo4;		//活四
    int huo3;		//活三
    int huo2;		//活二
    int chong4;		//冲四
    int chong3;		//冲三
    int chong2;		//冲二
    int []availablle_tag;		//可用格数
    int  []defense_direction; 	 //4个方向的防守危险度
    int []offense_direction;	//4个方向的进攻优先度
}
