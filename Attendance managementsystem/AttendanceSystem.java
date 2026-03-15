import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

// =================== ALL CLASSES IN ONE FILE ===================

// Parent Class
class Person implements Serializable {
    protected String id, name, email, password;
    public Person(String id,String name,String email,String password){
        this.id=id; this.name=name; this.email=email; this.password=password;
    }
    public boolean login(String id,String password){ return false; }
    public String getId(){ return id; }
    public String getName(){ return name; }
}


// Student Class
class Student extends Person {
    private String rollNumber, department, year;
    private ArrayList<Attendance> attendanceRecord;
    public Student(String id,String name,String email,String password,String rollNumber,String department,String year){
        super(id,name,email,password);
        this.rollNumber=rollNumber; this.department=department; this.year=year;
        attendanceRecord=new ArrayList<>();
    }
    public String getRollNumber(){return rollNumber;}
    public String getDepartment(){return department;}
    public String getYear(){return year;}
    public ArrayList<Attendance> getAttendanceRecord(){return attendanceRecord;}
    public void addAttendance(Attendance a){attendanceRecord.add(a);}
    public double getAttendancePercentage(){
        if(attendanceRecord.size()==0) return 0;
        long present=attendanceRecord.stream().filter(a->a.getStatus().equals("Present")).count();
        return (present*100.0)/attendanceRecord.size();
    }
    @Override
    public boolean login(String id,String password){return this.id.equals(id) && this.password.equals(password);}
}

// Admin Class
class Admin extends Person {
    public Admin(String id,String name,String email,String password){super(id,name,email,password);}
    @Override
    public boolean login(String id,String password){return this.id.equals(id) && this.password.equals(password);}
}

// Attendance Class
class Attendance implements Serializable {
    private String studentId, subject;
    private LocalDate date;
    private String status;
    public Attendance(String studentId,String subject,LocalDate date,String status){
        this.studentId=studentId; this.subject=subject; this.date=date; this.status=status;
    }
    public String getStudentId(){return studentId;}
    public String getSubject(){return subject;}
    public LocalDate getDate(){return date;}
    public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
}

// AttendanceManager Class
class AttendanceManager {
    private ArrayList<Student> students;
    private ArrayList<Attendance> attendanceRecords;
    public AttendanceManager(){
        students=new ArrayList<>();
        attendanceRecords=new ArrayList<>();
        loadData();
    }
    public void addStudent(Student s){students.add(s); saveData();}
    public void deleteStudent(String id){
        students.removeIf(s->s.getId().equals(id));
        attendanceRecords.removeIf(a->a.getStudentId().equals(id));
        saveData();
    }
    public Student searchStudent(String id){
        return students.stream().filter(s->s.getId().equals(id)).findFirst().orElse(null);
    }
    public ArrayList<Student> getStudents(){return students;}
    public void markAttendance(String studentId,String subject,String status){
        Attendance a=new Attendance(studentId,subject,LocalDate.now(),status);
        attendanceRecords.add(a);
        Student s=searchStudent(studentId);
        if(s!=null) s.addAttendance(a);
        saveData();
    }
    public void saveData(){
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("data.ser"))){
            out.writeObject(students);
            out.writeObject(attendanceRecords);
        }catch(Exception e){e.printStackTrace();}
    }
    @SuppressWarnings("unchecked")
    public void loadData(){
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream("data.ser"))){
            students=(ArrayList<Student>)in.readObject();
            attendanceRecords=(ArrayList<Attendance>)in.readObject();
        }catch(Exception e){
            students=new ArrayList<>();
            attendanceRecords=new ArrayList<>();
        }
    }
}
public class AttendanceSystem {
	private AttendanceManager manager;
    private JFrame loginFrame, adminFrame;
    private JTextField idField, emailField;
    private JPasswordField passwordField;

    public AttendanceSystem(){
        manager=new AttendanceManager();
        showLogin();
    }

    public void showLogin(){
        loginFrame=new JFrame("Attendance Management - Login");
        loginFrame.setSize(400,300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(5,2,10,10));

        loginFrame.add(new JLabel("User ID:")); idField=new JTextField(); loginFrame.add(idField);
        loginFrame.add(new JLabel("Email:")); emailField=new JTextField(); loginFrame.add(emailField);
        loginFrame.add(new JLabel("Password:")); passwordField=new JPasswordField(); loginFrame.add(passwordField);

        JButton adminLogin=new JButton("Admin Login");
        JButton studentLogin=new JButton("Student Login");
        loginFrame.add(adminLogin); loginFrame.add(studentLogin);

        adminLogin.addActionListener(e->{
            String id=idField.getText();
            String pwd=new String(passwordField.getPassword());
            Admin admin=new Admin("admin","Admin","admin@example.com","1234");
            if(admin.login(id,pwd)){ loginFrame.dispose(); showAdminDashboard(); }
            else JOptionPane.showMessageDialog(loginFrame,"Invalid Admin Login");
        });

        studentLogin.addActionListener(e -> {
            String id = idField.getText();
            String pwd = new String(passwordField.getPassword());
            Student s = manager.searchStudent(id);

            if (s != null && s.login(id, pwd)) {
                loginFrame.dispose();
                showStudentDashboard(s);
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid Student Login");
            }
        });


        loginFrame.setVisible(true);
    }

    public void showAdminDashboard(){
        adminFrame=new JFrame("Admin Dashboard");
        adminFrame.setSize(800,600);
        adminFrame.setLayout(new BorderLayout());

        String[] cols={"ID","Name","Roll No","Dept","Year","Attendance%"};
        DefaultTableModel model=new DefaultTableModel(cols,0);
        JTable table=new JTable(model){
            public Component prepareRenderer(TableCellRenderer renderer,int row,int col){
                Component c=super.prepareRenderer(renderer,row,col);
                if(col==5){
                    double val=(double)getValueAt(row,5);
                    if(val>=90) c.setBackground(Color.GREEN);
                    else if(val>=75) c.setBackground(Color.YELLOW);
                    else c.setBackground(Color.RED);
                }else c.setBackground(Color.WHITE);
                return c;
            }
        };
        refreshTable(model);

        adminFrame.add(new JScrollPane(table),BorderLayout.CENTER);

        JPanel buttons=new JPanel();
        JButton add=new JButton("Add Student");
        JButton delete=new JButton("Delete Student");
        JButton mark=new JButton("Mark Attendance");
        JButton refresh=new JButton("Refresh Table");
        buttons.add(add); buttons.add(delete); buttons.add(mark); buttons.add(refresh);
        adminFrame.add(buttons,BorderLayout.SOUTH);

        add.addActionListener(e->addStudentGUI(model));
        delete.addActionListener(e->deleteStudentGUI(model,table));
        mark.addActionListener(e->markAttendanceGUI(model,table));
        refresh.addActionListener(e->refreshTable(model));

        adminFrame.setVisible(true);
    }
    public void showStudentDashboard(Student s) {
        JFrame studentFrame = new JFrame("Student Dashboard - " + s.getName());
        studentFrame.setSize(750, 520);
        studentFrame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Subject-wise Attendance", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        studentFrame.add(title, BorderLayout.NORTH);

        String[] cols = {"Subject", "Present Hours", "Total Hours", "Attendance %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (col == 3) {
                    double val = (double) getValueAt(row, 3);
                    if (val >= 90) c.setBackground(Color.GREEN);
                    else if (val >= 75) c.setBackground(Color.YELLOW);
                    else c.setBackground(Color.RED);
                } else c.setBackground(Color.WHITE);
                return c;
            }
        };

        // ----- Calculate subject-wise data -----
        Map<String, Integer> presentCount = new HashMap<>();
        Map<String, Integer> totalCount = new HashMap<>();

        for (Attendance a : s.getAttendanceRecord()) {
            String subject = a.getSubject();
            totalCount.put(subject, totalCount.getOrDefault(subject, 0) + 1);
            if (a.getStatus().equalsIgnoreCase("Present"))
                presentCount.put(subject, presentCount.getOrDefault(subject, 0) + 1);
        }

        int totalPresent = 0, totalAll = 0;

        for (String subject : totalCount.keySet()) {
            int total = totalCount.get(subject);
            int present = presentCount.getOrDefault(subject, 0);
            double percent = (present * 100.0) / total;
            model.addRow(new Object[]{subject, present, total, percent});

            totalPresent += present;
            totalAll += total;
        }

        studentFrame.add(new JScrollPane(table), BorderLayout.CENTER);

        // ----- Overall summary -----
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));

        double overallPercent = totalAll == 0 ? 0 : (totalPresent * 100.0 / totalAll);
        JLabel countLabel = new JLabel(
            "Total Present: " + totalPresent + "    |    Total Classes: " + totalAll,
            SwingConstants.CENTER
        );
        countLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel overallLabel = new JLabel(
            "Overall Attendance: " + String.format("%.2f", overallPercent) + " %",
            SwingConstants.CENTER
        );
        overallLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Color code overall %
        if (overallPercent >= 90) overallLabel.setForeground(Color.GREEN.darker());
        else if (overallPercent >= 75) overallLabel.setForeground(Color.ORANGE.darker());
        else overallLabel.setForeground(Color.RED.darker());

        summaryPanel.add(countLabel);
        summaryPanel.add(overallLabel);
        studentFrame.add(summaryPanel, BorderLayout.SOUTH);

        studentFrame.setVisible(true);
    }



    private void refreshTable(DefaultTableModel model){
        model.setRowCount(0);
        for(Student s: manager.getStudents()){
            model.addRow(new Object[]{s.getId(),s.getName(),s.getRollNumber(),s.getDepartment(),s.getYear(),s.getAttendancePercentage()});
        }
    }

    private void addStudentGUI(DefaultTableModel model){
        JTextField idF=new JTextField(); JTextField nameF=new JTextField(); JTextField emailF=new JTextField();
        JTextField pwdF=new JTextField(); JTextField rollF=new JTextField(); JTextField deptF=new JTextField(); JTextField yearF=new JTextField();
        Object[] fields={"ID:",idF,"Name:",nameF,"Email:",emailF,"Password:",pwdF,"Roll No:",rollF,"Department:",deptF,"Year:",yearF};
        int res=JOptionPane.showConfirmDialog(null,fields,"Add Student",JOptionPane.OK_CANCEL_OPTION);
        if(res==JOptionPane.OK_OPTION){
            Student s=new Student(idF.getText(),nameF.getText(),emailF.getText(),pwdF.getText(),rollF.getText(),deptF.getText(),yearF.getText());
            manager.addStudent(s);
            refreshTable(model);
        }
    }

    private void deleteStudentGUI(DefaultTableModel model,JTable table){
        int row=table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(adminFrame,"Select a student to delete"); return; }
        String id=(String)table.getValueAt(row,0);
        manager.deleteStudent(id);
        refreshTable(model);
    }

    private void markAttendanceGUI(DefaultTableModel model,JTable table){
        String subject=JOptionPane.showInputDialog("Enter Subject:");
        for(Student s: manager.getStudents()){
            int res=JOptionPane.showConfirmDialog(null,"Mark "+s.getName()+" as Present?","Attendance",JOptionPane.YES_NO_OPTION);
            manager.markAttendance(s.getId(),subject,res==JOptionPane.YES_OPTION?"Present":"Absent");
        }
        refreshTable(model);
    }
	public static void main(String[] args) {

		new AttendanceSystem();

	}
}