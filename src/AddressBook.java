import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.*;

public class AddressBook extends JFrame {
    private ArrayList<Contact> contacts;
    private JTable contactTable;
    private DefaultTableModel tableModel;

    public AddressBook() {
        super("주소록");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        contacts = new ArrayList<>();
        loadContacts();

        // 테이블 모델 생성 및 초기화
        tableModel = new DefaultTableModel(new Object[]{"이름", "전화번호", "이메일"}, 0);
        updateContactTable();

        // 테이블 생성 및 추가
        contactTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널 생성 및 추가
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("추가");
        JButton deleteButton = new JButton("삭제");
        JButton viewButton = new JButton("자세히 보기");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 이벤트 리스너 등록
        addButton.addActionListener(e -> addContact());
        deleteButton.addActionListener(e -> deleteContact());
        viewButton.addActionListener(e -> viewContact());

        // 창 닫기 이벤트 리스너 등록
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmExit()) {
                    saveContacts();
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }

    // 주소록 추가
    private void addContact() {
        String name = JOptionPane.showInputDialog(this, "이름 입력:");
        String phone = "";
        String email = "";

        while (true) {
            phone = JOptionPane.showInputDialog(this, "전화번호 입력 (형식: 010-1234-5678):");
            if (isValidPhoneNumber(phone)) {
                break;
            } else {
                JOptionPane.showMessageDialog(this, "전화번호를 형식에 맞게 입력해 주세요. (형식: 010-1234-5678 ).");
            }
        }

        while (true) {
            email = JOptionPane.showInputDialog(this, "이메일 입력 (형식: ID@xxxx.xxx):");
            if (isValidEmail(email)) {
                break;
            } else {
                JOptionPane.showMessageDialog(this, "이메일을 형식에 맞게 입력해 주세요. (형식: ID@xxxx.xxx)");
            }
        }

        if (name != null && phone != null && email != null) {
            Contact contact = new Contact(name, phone, email);
            contacts.add(contact);
            updateContactTable();
        }
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {
            if (confirmDelete()) {
                contacts.remove(selectedRow);
                updateContactTable();
            }
        }
    }

    private void viewContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {
            Contact contact = contacts.get(selectedRow);
            JOptionPane.showMessageDialog(this, contact.toString(), "연락처 보기", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateContactTable() {
        tableModel.setRowCount(0);
        for (Contact contact : contacts) {
            tableModel.addRow(new Object[]{contact.getName(), contact.getPhone(), contact.getEmail()});
        }
    }

    private void loadContacts() {
        try {
            FileInputStream fis = new FileInputStream("contacts.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            contacts = (ArrayList<Contact>)ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // 파일 로드 실패 시 새 ArrayList 생성
            contacts = new ArrayList<>();
        }
    }

    private void saveContacts() {
        try {
            FileOutputStream fos = new FileOutputStream("contacts.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(contacts);
            oos.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "연락처 저장에 실패하였습니다.", "에러", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean confirmDelete() {
        int result = JOptionPane.showConfirmDialog(this, "연락처를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private boolean confirmExit() {
        int result = JOptionPane.showConfirmDialog(this, "프로그램을 종료하시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^\\d{3}-\\d{4}-\\d{4}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(phoneNumber);
        return m.matches();
    }

    private static boolean isValidEmail(String email) {
        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        return m.matches();
    }

    private static class Contact implements Serializable {
        private String name;
        private String phone;
        private String email;

        public Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "이름: " + name + "\n전화번호: " + phone + "\n이메일: " + email;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddressBook::new);
    }
}
