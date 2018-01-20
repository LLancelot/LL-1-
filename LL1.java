import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;


//////////界面可视化设计//////////////////
public class LL1 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame f;
	JTextField tf1; 
	JTextField tf2;
	JLabel l;
	JButton b0;
	JPanel p1, p2, p3;
	JTextArea t1, t2, t3;
	JButton b1, b2, b3;
	JLabel l0, l1, l2, l3, l4;
	JTable table;

	DefaultTableModel dtm;
	String Vn[] = null;         //所有非终结符集合
	Vector<String> P = null;    //字符串型向量，用于填预测分析表
	
	int firstComplete[] = null; // 存储已判断过 first 的数据
	char first[][] = null;      // 存储最后 first 结果
	
	int followComplete[] = null;// 存储已判断过 follow 的数据
	char follow[][] = null;     // 存储最后 follow 结果
	
	char select[][] = null;     // 存储最后 select 结果	
	int LL = 0;                 // 标记是否为 LL（1）	
	String Vt_table[] = null;		// 储存 Vt
	Object Vn_data[][] = null;	// 存储表达式数据
	char yn_null[] = null;		// 存储能否推出空

	LL1() {
		super("LL(1)文法分析器");
		setLocation(300, 0);
		setSize(760, 780);		
		tf1 = new JTextField(20);		//产生式左部
		tf2 = new JTextField(20);		//产生式右部
		l = new JLabel("→");
		l0 = new JLabel("输入字符串：");
		l2 = new JLabel(" ");
		l4 = new JLabel("预测分析表：");
		p1 = new JPanel();
		p2 = new JPanel();
		t1 = new JTextArea(24, 20);		//文法显示
		t2 = new JTextArea(1, 30);		//待输入句子
		t3 = new JTextArea(24, 40);		//文法分析及判断待输入句子是否为文法句型
		b0 = new JButton("添加产生式");	//此处默认S为产生式开始符
		b1 = new JButton("判断文法 ");
		b2 = new JButton("句子判断");
		b3 = new JButton("清空全部");
		table = new JTable();
		JScrollPane jp1 = new JScrollPane(t1);
		JScrollPane jp2 = new JScrollPane(t2);
		JScrollPane jp3 = new JScrollPane(t3);
		p1.add(tf1);
		p1.add(l);
		p1.add(tf2);
		p1.add(b0);
		p1.add(b1);
		p1.add(l0);
		p1.add(l2);
		p1.add(jp2);
		p1.add(b2);
		p1.add(b3);
	//	p2.add(l1);
	//	p2.add(l3);
		p1.add(jp1);
		p1.add(jp3);
		p2.add(l4);
		p2.add(new JScrollPane(table));
		add(p1, "Center");
		add(p2, "South");
		b0.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		table.setPreferredScrollableViewportSize(new Dimension(660, 200));
		setVisible(true);
	}
	
//////界面设计结束，按键响应函数及分析代码如下///////
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == b0)//"输入产生式"
		{
			String a = tf1.getText();
			String b = tf2.getText();
			t1.append(a + '→' + b + '\n');//将所输入的产生式添加到文本区
		}
		///////////////////////////////////////
		if (e.getSource() == b1) //判断文法按钮，有三集操作
		{
			t3.setText("");//每次判断都要清空上次显示的内容
			int Vnnum = 0;//非终结符个数
			int k;
			Vn = new String[100];//非终结符集合（字符串类型）
			P = new Vector<String>();//字符串向量
			
			String s[] = t1.getText().split("\n");//依次读取文本框中的每条产生式
			for (int i = 0; i < s.length; i++) //s.length为产生式的条数
			{
				if (s.length < 2)//有误情况 
				{
					t3.setText("文法输入有误，请重新输入");// 判断产生式长度是否符合
					return;
				}
				
				if (s[i].charAt(0) <= 'Z' && s[i].charAt(0) >= 'A' && s[i].charAt(1) == '→')//产生式一般情况
				{
					for (k = 0; k < Vnnum; k++)//逐一查找非终结符，相同的终结符则跳过
					{
						if (Vn[k].equals(s[i].substring(0, 1))) 
						{
							break;
						}
					}
					if (Vnnum == 0 || k >= Vnnum)//找到新的非终结符，加入Vn[]
					{
						Vn[Vnnum] = s[i].substring(0, 1);// 产生式左部存入 Vn数组
						Vnnum++;//统计Vn的总数
					}
					P.add(s[i]);//将第i行产生式添加到向量p中
				}
				
				else//产生式左部不是非终结符
				{
					t3.setText("文法输入有误，请重新输入");
					return;
				}
			}//end for (i = 0; i < s.length; i++)
			
			yn_null = new char[100];
			first = new char[Vnnum][100];//每个非终结符FIRST集合，二维数组，Vnnum由上述操作得到非终结符个数
			int flag = 0;
			String firstVn[] = null;
			firstComplete = new int[Vnnum];// 存储已判断过 first 的数据
			
			for (int i = 0; Vn[i] != null; i++) //依次对每个Vn求 FIRST集
			{
				flag = 0;
				firstVn = new String[20];
				if ((flag = addFirst(first[i], Vn[i], firstVn, flag)) == -1)
					//(char a[], String b, String firstVn[], int flag)
					return;
				firstComplete[i] = 1;//if flag不为-1，则表示已经完成一个Vn的first集
			}
			///////////////////////////////////////////////////
			t3.append("FIRST 集：" + "\n"); // 显示 FIRST集
			for (int i = 0; Vn[i] != null; i++) 
			{
				t3.append("FIRST(" + Vn[i] + ")={ " + first[i][0]);
				for (int j = 1; first[i][j] != '\0'; j++)
				{
					t3.append(" , " + first[i][j] );
				}
				t3.append(" }" + "\n");
			}
			t3.append("\n");
			
			///////END FIRST//////
			
			follow = new char[Vnnum][100];  	//存储最后 follow 结果
			String followVn[] = null;       	// 存储已判断过 follow 的数据
			followComplete = new int[Vnnum];
			for (int i = 0; Vn[i] != null; i++) // 求 FOLLOW
			{
				flag = 0;
				followVn = new String[20];
				if ((flag = addFollow(follow[i], Vn[i], followVn, flag)) == -1)
					//addFollow(char a[], String b, String followVn[], int flag)					
					return;
				followComplete[i] = 1;
			}
			t3.append("FOLLOW 集：" + "\n"); // 显示 FOLLOW
			for (int i = 0; Vn[i] != null; i++) {
				t3.append("FOLLOW(" + Vn[i] + ")={ " + follow[i][0]);
				for (int j = 1; follow[i][j] != '\0'; j++) {
					t3.append( " , " + follow[i][j]);
				}
				t3.append(" }" + "\n");
			}
			t3.append("\n");
			///////END FOLLOW//////
			
			select = new char[P.size()][100];  //P.size()是产生式的数目，每个产生式都有一个select集
			for (int i = 0; i < P.size(); i++) // 求 SELECT
			{
				flag = 0;//初值为0
				addSelect(select[i], (String) P.elementAt(i), flag);
			}
			t3.append("SELECT 集：" + "\n"); // 显示 SELECT
			for (int i = 0; i < P.size(); i++) {
				t3.append("SELECT(" + (String) P.elementAt(i) + ")={ " + select[i][0]);
				for (int j = 1; select[i][j] != '\0'; j++) {
					t3.append(" , " + select[i][j]);
				}
				t3.append(" }" + "\n");
			}
			t3.append("\n");
			///////end select in text//////
			
			
			for (int i = 0; Vn[i] != null; i++)// 判断 select交集是否为空
			{
				int temp = 0;
				char save[] = new char[100];
				for (int j = 0; j < P.size(); j++)
				{
					String t = (String) P.elementAt(j);
					if (t.substring(0, 1).equals(Vn[i])) 
					{
						for (k = 0; select[j][k] != '\0'; k++)
						{
							if (inChar(save, select[j][k])) //逐一添加到save中，有重复的就不执行添加
							{
								save[temp] = select[j][k];
								temp++; 
							}
							else// 有重复情况，即当有交集时，不为 LL1文法
							{
								t3.append("不是 LL（1）文法！！" + "\n");
								t3.append("因为存在相同左部其产生式的SELECT集的交集不为空！");
								dtm = new DefaultTableModel();
								table.setModel(dtm);								
								return;
							}
						}
					}
				}
			}
			
			char Vt[] = new char[100];//终结符集
			int temp = 0;
			for (int i = 0; i < P.size(); i++)
			{
				String t = (String) P.elementAt(i);//取出一条产生式
				for (int j = 2; j < t.length(); j++)// 提取表达式右侧的终结符存入 Vt
				{
					if (t.charAt(j) > 'Z' || t.charAt(j) < 'A') //终结符
					{
						if (inChar(Vt, t.charAt(j))) 
						{
							Vt[temp] = t.charAt(j);
							temp++;
						}
					}
				}
			}
			if (inChar(Vt, '#'))				// 若可推出空集，则将#加入 Vt。
			{
				Vt[temp] = '#';
				temp++;
			}
			Vt_table = new String[temp + 1];// 根据 select 和表达式生成预测分析表，temp为终结符个数，表头第一行一列做空
			Vn_data = new String[Vnnum][temp + 1];//头为所有Vn
			String f = "";//初始化
			Vt_table[0] = f;
			for (int i = 0; i < temp; i++) 
			{
				Vt_table[i + 1] = String.valueOf(Vt[i]);
			}
			for (int i = 0; i < Vnnum; i++) 
			{
				Vn_data[i][0] = Vn[i];
			}
			for (int i = 0; i < P.size(); i++)
			{
				String t = (String) P.elementAt(i);
				int j;
				for (j = 0; j < Vn.length; j++) 
				{
					if (Vn[j].equals(t.substring(0, 1))) //找到下标
					{
						break;
					}
				}
				for (k = 0; select[i][k] != '\0'; k++) {
					int y = pos(Vt, select[i][k]); //返回SELECT集元素在Vt的位置
					Vn_data[j][y + 1] = t.substring(1);//将相应位置填入表
				}
			}
			dtm = new DefaultTableModel(Vn_data, Vt_table);// 显示预测分析表
			table.setModel(dtm);//显示
			LL = 1;
		}
		//////////////////////////////////////////
		if (e.getSource() == b3)// 清空列表
		{
			tf1.setText("");
			tf2.setText("");
			t1.setText("");
			t2.setText("");
			t3.setText("");
			Vn = null;
			P = null;
			firstComplete = null;
			first = null;
			followComplete = null;
			follow = null;
			select = null;
			dtm = new DefaultTableModel();
			table.setModel(dtm);
		}
		/////////////////////////////////////
		if (e.getSource() == b2)// 输入字符串并预测分析
		{
			t3.setText("");
			if (LL == 1)			//如果满足LL1文法 
			{
				String s = t2.getText(); //得到句子
				for (int i = 0; i < s.length(); i++) 
				{
					if (s.charAt(i) == '\0') 
					{
						t3.setText("字符串中请不要加入空格" + "\n");
						return;
					}
				}
				char Sentence[] = new char[100];// 剩余输入串
				char Analyze[] = new char[100];// 分析栈
				Sentence[0] =  '#';
				Analyze[1] = 'S';
				Analyze[0] = '#';
				int n_Sentence  = 1;//0号位存#,从第1位开始
				int n_Analyze = 2;
				for (int i = s.length() - 1; i >= 0; i--) 
				{
					Sentence[n_Sentence] = s.charAt(i);//将输入的句子倒序存入字符串
					n_Sentence++;
				}
				//此时n_Sentence - 1为字符真实长度
				int step = 2;
				char n[] = new char[65];// 存储要显示的一整行内容
										//1      S#        i+i*i#       S→E
				t3.append("步骤                  分析栈                  剩余输入串                  所用产生式或匹配" + "\n");
				n[0] = '1';
				n[15] = '#';
				n[14] = 'S';//第一行初始化
				int u = 29;
				for (int i = n_Sentence - 1; i >= 0; i--) //把剩余字符串加入到某一行中
				{
					n[u] = Sentence[i];
					u++;
				}
				while (!(Analyze[n_Analyze - 1] == '#' && Sentence[n_Sentence - 1] == '#'))// 剩余输入串不为#则分析
				{
					int i, j;
					char t = Sentence[n_Sentence - 1];
					char k = Analyze[n_Analyze - 1];
					if (t == k)// 产生式匹配
					{
						n[49] = k;
						n[50] = '匹';
						n[51] = '配';
						t3.append(String.copyValueOf(n) + "\n");
						n = new char[65];
						n_Sentence--;
						n_Analyze--;
						if (step < 10)
							n[0] = (char) ('0' + step);// 显示步骤数
						else {
							n[0] = (char) ('0' + step / 10);
							n[1] = (char) ('0' + step % 10);
						}
						u = 14;
						for (int y = n_Analyze - 1; y >= 0; y--)// 处理分析栈，出栈
						{
							n[u] = Analyze[y];
							u++;
						}
						u = 29;
						for (int y = n_Sentence - 1; y >= 0; y--)// 处理剩余字符串，消除一个字符
						{
							n[u] = Sentence[y];
							u++;
						}
						step++;
						continue;
					}
					for (i = 0; Vn[i] != null; i++)// 搜寻所用产生式的左部
					{
						if (Vn[i].equals(String.valueOf(k)))
							break;
					}
					for (j = 0; j < Vt_table.length; j++)// 判断是否匹配
					{
						if (Vt_table[j].equals(String.valueOf(t)))
							break;
					}
					if (j >= Vt_table.length)// 全部产生式都不能符合则报错
					{
						t3.append(String.copyValueOf(n));
						t3.append("\n" + "该串不是该文法的句型！" + "\n");
						return;
					}
					Object result1 = Vn_data[i][j];
					if (result1 == null) {
						t3.append(String.copyValueOf(n));
						t3.append("\n" + "该串不是该文法的句型！" + "\n");
						return;
					} 
					else// 找到所用产生式
					{
						n[49] = Vn[i].charAt(0);
						u = 50;
						String result = (String) result1;
						for (int y = 0; y < result.length(); y++) {
							n[u] = result.charAt(y);
							u++;
						}
						t3.append(String.copyValueOf(n) + "\n");
						n = new char[65];//新一行
						n_Analyze--;
						for (i = result.length() - 1; i > 0; i--)// 将分析栈内非终结符换为右边表达式
						{
							if (result.charAt(i) != '#') {
								Analyze[n_Analyze] = result.charAt(i);
								n_Analyze++;
							}
						}
					}
					if (step < 10)// 显示“步骤”
						n[0] = (char) ('0' + step);
					else {
						n[0] = (char) ('0' + step / 10);
						n[1] = (char) ('0' + step % 10);
					}
					u = 14;
					for (int y = n_Analyze - 1; y >= 0; y--) {
						n[u] = Analyze[y];
						u++;
					}
					u = 29;
					for (int y = n_Sentence - 1; y >= 0; y--) {
						n[u] = Sentence[y];
						u++;
					}
					step++;
				}
				n = new char[65];
				n[0] = '$';n[14] = '#';n[29] = '#';
				n[49] = '接';
				n[50] = '受';
				n[51] = '!';
				t3.append(String.copyValueOf(n));
				t3.append("\n" + "该串是此文法的句型！" + "\n");
				return;
			}
			if(LL == 0)
			{
				t3.setText("本文法不是LL(1)文法，无法分析，请更改文法！");
			}
			else 
			{
				t3.setText("请先依次输入文法，并点击文法判断按钮" + "\n");
				return;
			}
		}
	}
	
	////响应函数结束，功能函数如下/////
	private int addFirst(char a[], String b, String firstVn[], int flag)//添加first集
	//addFirst(first[i], Vn[i], firstVn, flag)
	// 计算 FIRST**（递归）
	{
		if (inString(firstVn, b.charAt(0))) //判断firstVn字符串中是否存在b的第一个字符，不存在为true
			//boolean inString(String a[], char b)
		{
			addString(firstVn, b);// 不在，把  第i个Vn加入字符串组 firstVn[]
		} 
		else
		{
			return flag;
		}
		
		for (int i = 0; i < P.size(); i++) //P.size()是产生式的数目
		{
			String t = (String) P.elementAt(i);//向量第i处元素赋值给字符串t，t为产生式字符串
			for (int k = 2; k < t.length(); k++)//t.length()是本条产生式的长度，从产生式右边第一个字符开始判断（T->+SB）
			{
				if (t.substring(0, 1).equals(b)) //向量第i条产生式首字符与Vn[i]相等
				{
					if (t.charAt(k) > 'Z' || t.charAt(k) < 'A')// 遇到的是终结符（不是A~Z）,继续																
					{
						if (flag == 0 || inChar(a, t.charAt(k))) //表达式右端第 k-1个字符不在该Vn的first集中，避免重复添加
																 //例如A->aB,A->aC
						{
							if (t.charAt(k) == '#')// 空时
							{
								if (k + 1 == t.length()) //是最后一个字符
								{
									a[flag] = t.charAt(k);//加入该Vn的first集
									flag++;
								}
								int flag1 = 0;
								for (int j = 0; yn_null[j] != '\0'; j++)// 所求Vn进入yn_null[]
								{
									if (yn_null[j] == b.charAt(0))// 循环判断Vn[i]能否推出空
									{
										flag1 = 1;
										break;//已经在循环查找时把能推出空的Vn加入了yn_null[]，不需要再执行
									}
								}
								if (flag1 == 0)//没找到，则需要把能推出空的Vn加入yn_null[]
								{
									int j;
									for (j = 0; yn_null[j] != '\0'; j++) {
									}
									yn_null[j] = b.charAt(0);//让j在yn_null[]的最后一个位置，加入Vn
								}
								continue;//继续右移查找下一个字符
							} 
							//end if (t.charAt(k) == '#')
							else// 如果不为空，则终结符直接加入 first[],即a[]
							{
								a[flag] = t.charAt(k);
								flag++;
								break;
							}
						}
						break;
					}//end if(t.charAt(k) > 'Z' || t.charAt(k) < 'A')
					else // 遇到的第一个字符为非终结符的情况
					{
						if (!inString(Vn, t.charAt(k))) //如果检索到的该字符是Vn
							//boolean inString(String a[], char b)
						{
							int p = firstComplete(t.charAt(k));// 当该非终结符的 first集已经求出
																
							if (p != -1) //已经完成first判断
							{
								flag = addElementFirst(a, p, flag);// 直接加入所求first
																	
							}
							//如果还没有完成判断
							else if ((flag = addFirst(a, String.valueOf(t.charAt(k)), firstVn, flag)) ==-1)//递归
								return -1;
							int flag1 = 0;
							for (int j = 0; yn_null[j] != '\0'; j++)// 当非终结符的first集有#															
							{
								if (yn_null[j] == t.charAt(k)) {
									flag1 = 1;
									break;
								}
							}
							if (flag1 == 1)// 当非终结符的 first 能推出空
							{
								if (k + 1 == t.length() && inChar(a, '#'))// 之后无符号，且所求first集中无空。如S->A,A->#																			
								{
									a[flag] = '#';// first 中加入#
									flag++;
								}
								continue;// 判断下一个字符
							} 
							else 
							{
								break;
							}
						} 
						//endif(!inString(Vn, t.charAt(k))) //如果检索到的该字符是Vn
						else// 检索的该字符为其他
						{
							t3.setText("文法输入有误" + "\n");
							return -1;
						}
					}//非终结符情况
				}
			}//一条产生式处理完毕
		}//所有产生式处理完毕
		return flag;
	}

	private int addFollow(char a[], String b, String followVn[], int flag)
	            //addFollow(follow[i], Vn[i], followVn, flag)
	// 计算 FOLLOW（递归）
	{
		if (inString(followVn, b.charAt(0))) {
			addString(followVn, b);
		}
		else 
		{
			return flag;
		}
		if (b.equals("S"))											  // 当为 S 时#直接存入 follow
		{
			a[flag] = '#';
			flag++;
		}
		for (int i = 0; i < P.size(); i++) 
		{
			String t = (String) P.elementAt(i);
			for (int j = 2; j < t.length(); j++) {
				if (t.charAt(j) == b.charAt(0) && j + 1 < t.length()) //产生式右端的该Vn不是最后一个字符
				{
					if (t.charAt(j + 1) != '\0') 
					{
						if ((t.charAt(j + 1) > 'Z' || t.charAt(j + 1) < 'A'))// 该Vn后一位是终结符，例如S->Ac的c
						{
							if (flag == 0 || inChar(a, t.charAt(2)))
							{
								a[flag] = t.charAt(j + 1);//将后一位加入FOLLOW集
								flag++;
							}
						} 
						else//后一位为Vn
						{
							int k;
							for (k = 0; Vn[k] != null; k++)
							{
								if (Vn[k].equals(String.valueOf(t.charAt(j + 1))))
								{
									break;// 找寻下一个非终结符的 Vn 位置
								}
							}
							//找到了下标k
							flag = addElementFirst(a, k, flag);// 把下一个非终结符 first加入所求 follow 集，a此时为follow[]
																
							for (k = j + 1; k < t.length(); k++) 
							{
								if ((t.charAt(j + 1) > 'Z' || t.charAt(j + 1) < 'A'))//后一位不是Vn，不符合，break
									break;
								else
								{
									if (isEmpty(t.charAt(k))) //下一个非终结符可推出空，把表达式左边Vn的follow 集加入所求 follow 集
									{
										int p = followComplete(t.charAt(0));//得到产生式左部Vn的下标
										if (p != -1) 
										{
											flag = addElementFollow(a, p, flag);//flag用于递归判断
										} else if ((flag = addFollow(a, String.valueOf(t.charAt(0)), followVn, flag)) == -1)//递归
											return -1;
									} 
									else 
									{
										break;
									}
								}
							}
						}
					} 
					else	// 错误文法
					{
						t3.setText("文法输入有误,请重新输入" + "\n");
						return -1;
					}
				}
				if (t.charAt(j) == b.charAt(0) && j + 1 == t.length())
					//右边只有一个Vn
					//下一个非终结符可推出空，把表达式左边Vn的follow 集加入所求 follow 集
					//例如A->B,FOLLOW(A)加入FOLLOW(B)
				{
					int p = followComplete(t.charAt(0));//找到左部Vn的下标p
					if (p != -1) 
					{
						flag = addElementFollow(a, p, flag);//把下标为p的Vn的FOLLOW集加入到当前FOLLOW集
					} else if ((flag = addFollow(a, String.valueOf(t.charAt(0)), followVn, flag)) == -1)
						return -1;
				}
			}
		}
		return flag;
	}

	private void addSelect(char a[], String b, int flag) // 计算 SELECT
				//addSelect(select[i], (String) P.elementAt(i), flag);
	{
		int i = 2;//i用于定位，起始位置为右端第一个字符
		int temp = 0;
		while (i < b.length()) // b.length()是一条产生式的长度
		{
			if ((b.charAt(i) > 'Z' || b.charAt(i) < 'A') && b.charAt(i) != '#')// 是终结符，且不为空，例如A->b 或 A->+...
			{
				a[flag] = b.charAt(i);// 将这个字符加入到 Select集，结束 Select集 的计算
				break;
			} 
			else if (b.charAt(i) == '#')// 是空
			{
				int j;
				for (j = 0; Vn[i] != null; j++)// 将表达式左侧的非终结符的 follow 加入 select，例如A->#，则需计算FOLLOW(A)
				{
					if (Vn[j].equals(b.substring(0, 1))) //找到产生式左部Vn的下标j
					{
						break;
					}
				}
				for (int k = 0; follow[j][k] != '\0'; k++) //依次把左部Vn的Follow集加入到本产生式的select集
				{
					if (inChar(a, follow[j][k])) {
						a[flag] = follow[j][k];
						flag++;
					}
				}
				break;
			} 
			else if (b.charAt(i) >= 'A' && b.charAt(i) <= 'Z' && i + 1 < b.length())// 是Vn，且有下一个字符
			{
				int j;
				for (j = 0; Vn[i] != null; j++) {
					if (Vn[j].equals(String.valueOf(b.charAt(i))))//逐一与当前Vn比较找到下标j
					{
						break;//找到，结束循环
					}
				}
				for (int k = 0; first[j][k] != '\0'; k++) 
				{
					if (inChar(a, first[j][k]))// 把表达式右侧所有非终结符的 first 集加入
					{
						if (first[j][k] == '#')// first 中存在空，即右侧的Vn能推出空
						{
							temp = 1;
						}
						else //右侧的Vn推不出空
						{
							a[flag] = first[j][k];//加入
							flag++;
						}
					}
				}
				if (temp == 1)// 把右侧所有非终结符的 first 中的#去除
				{
					i++;
					temp = 0;//改为0
					continue;
				}
				else 
				{
					temp = 0;
					break;
				}
			} 
			else if (b.charAt(i) >= 'A' && b.charAt(i) <= 'Z' && i + 1 >= b.length())// 是Vn且已经是最后一个字符
			{
				int j;
				for (j = 0; Vn[i] != null; j++) 
				{
					if (Vn[j].equals(String.valueOf(b.charAt(i)))) //固定Vn[i]找下标j
					{
						break;
					}
				}
				for (int k = 0; first[j][k] != '\0'; k++) 
				{
					if (inChar(a, first[j][k])) {
						if (first[j][k] == '#') 
						{
							temp = 1;// 表达式右侧能推出空，标记1
						} 
						else 
						{
							a[flag] = first[j][k];// 不能推出空，直接将 first 集加入 select集
													
							flag++;
						}
					}
				}
				if (temp == 1)// 表达式右侧能推出空，需要把左部的FOLLOW加入SELECT
				{
					for (j = 0; Vn[i] != null; j++) {
						if (Vn[j].equals(b.substring(0, 1))) //找下标j
						{
							break;
						}
					}
					for (int k = 0; follow[j][k] != '\0'; k++) {
						if (inChar(a, follow[j][k])) {
							a[flag] = follow[j][k];			//将表达式左侧Vn的follow 加入select													
							flag++;
						}
					}
					break;
				} 
				else 
				{
					temp = 0;
					break;
				}
			}
		}
	}

	// x在终结符集的位置
	private int pos(char Vt[], char x) {
		int i;
		for (i = 0; Vt[i] != '\0'; i++)
		{
			if (Vt[i] == x)
				break;
		}
		return i;
	}
	
	// 判断 x 是否在 字符串 a 中，在返回 false，不在返回 true
	private boolean inChar(char a[], char x) {
		for (int i = 0; a[i] != '\0'; i++) 
		{
			if (a[i] == x)
				return false;
		}
		return true;
	}

	// 判断字符 x 是否在字符串 a 中，在返回 false，不在返回 true
	private boolean inString(String a[], char x)
	{
		for (int i = 0; a[i] != null; i++)
		{
			if (a[i].equals(String.valueOf(x)))
				return false;//在
		}
		return true;//不在
	}

	// 把x加入字符串组 firstVn[]的最后
	private void addString(String firstVn[], String x) {
		int i;
		for (i = 0; firstVn[i] != null; i++) 
		{}
		firstVn[i] = x;
	}

	// 判断x 是否已完成 first
	private int firstComplete(char x) {
		int i;
		for (i = 0; Vn[i] != null; i++)
		{
			if (Vn[i].equals(String.valueOf(x))) //如果找到Vn[i]=x
			{
				if (firstComplete[i] == 1) 		 //如果完成判断，置1
					return i;				     //并返回该Vn的下标i
				else
					return -1;				     //没有完成判断，返回
			}
		}
		return -1;								//没有找到，返回
	}

	// 判断 x是否已完成 follow
	private int followComplete(char x) 
	{
		for (int i = 0; Vn[i] != null; i++) 
		{
			if (Vn[i].equals(String.valueOf(x)))	//如果找到Vn[i]=x
			{
				if (followComplete[i] == 1)			//如果完成判断，置1
					return i;						//并返回该Vn的下标i
				else
					return -1;						//没有完成判断，返回
			}
		}
		return -1;									//没有找到，返回
	}

	// 把相应终结符添加到first[]中, pos为该下标
	private int addElementFirst(char a[], int pos, int flag) {
		for (int i = 0; first[pos][i] != '\0'; i++) 
		{
			if (inChar(a, first[pos][i]) && first[pos][i] != '#') 
			{
				a[flag] = first[pos][i];
				flag++;//记录每个Vn的first集元素个数
			}
		}
		return flag;
	}

	// 把相应终结符添加到 follow[]中, pos为该下标
	private int addElementFollow(char a[], int pos, int flag) {
		for (int i = 0; follow[pos][i] != '\0'; i++) 
		{
			if (inChar(a, follow[pos][i])) //把元素逐一添加到FOLLOW[]中
			{
				a[flag] = follow[pos][i];
				flag++;
			}
		}
		return flag;
	}

	// 判断该Vn的FIRST集合是否有空
	private boolean isEmpty(char x) {
		int i;
		for (i = 0; Vn[i] != null; i++)
		{
			if (Vn[i].equals(String.valueOf(x))) //找到Vn的下标i
			{
				break;
			}
		}
		for (int j = 0; first[i][j] != '\0'; j++) //根据找到的i值查找对应Vn的FIRST集是否含空
		{
			if (first[i][j] == '#')//有空
				return true;
		}
		return false;
	}
	
	/////////////////////////////////////////////
	//所有功能函数结束
	public static void main(String[] args) {
		new LL1();
	}
}
