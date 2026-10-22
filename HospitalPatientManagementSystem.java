import java.util.*;

/**
 * Hospital Patient Management System
 * Demonstrates:
 * 1. LinkedList - dynamically stores patient records
 * 2. Queue (LinkedList) - regular consultation, FIFO
 * 3. PriorityQueue - emergency patients served by highest priority first
 *
 * Compile: javac HospitalPatientManagementSystem.java
 * Run:     java HospitalPatientManagementSystem
 */
public class HospitalPatientManagementSystem {

    static class Patient {
        private final int id;
        private String name;
        private int age;
        private String condition;
        private int emergencyPriority; // 1 = highest, larger number = lower priority

        Patient(int id, String name, int age, String condition, int emergencyPriority) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.condition = condition;
            this.emergencyPriority = emergencyPriority;
        }

        int getId() { return id; }
        String getName() { return name; }
        int getEmergencyPriority() { return emergencyPriority; }

        void update(String name, int age, String condition) {
            this.name = name;
            this.age = age;
            this.condition = condition;
        }

        void setEmergencyPriority(int priority) {
            this.emergencyPriority = priority;
        }

        @Override
        public String toString() {
            String priorityText = emergencyPriority == 0
                    ? "Regular"
                    : "Emergency (priority " + emergencyPriority + ")";
            return "ID: " + id + " | Name: " + name + " | Age: " + age
                    + " | Condition: " + condition + " | Type: " + priorityText;
        }
    }

    private final Scanner scanner = new Scanner(System.in);
    private final LinkedList<Patient> patientRecords = new LinkedList<>();
    private final Queue<Patient> regularQueue = new LinkedList<>();

    // Lower number means more urgent. ID breaks ties fairly by registration order.
    private final PriorityQueue<Patient> emergencyQueue = new PriorityQueue<>(
            Comparator.comparingInt(Patient::getEmergencyPriority)
                      .thenComparingInt(Patient::getId));

    private int nextId = 1;

    public static void main(String[] args) {
        new HospitalPatientManagementSystem().run();
    }

    private void run() {
        boolean running = true;
        System.out.println("======================================");
        System.out.println(" HOSPITAL PATIENT MANAGEMENT SYSTEM");
        System.out.println("======================================");

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: registerPatient(); break;
                case 2: addToRegularQueue(); break;
                case 3: addToEmergencyQueue(); break;
                case 4: serveEmergencyPatient(); break;
                case 5: serveRegularPatient(); break;
                case 6: searchPatient(); break;
                case 7: updatePatient(); break;
                case 8: removePatient(); break;
                case 9: displayAllPatients(); break;
                case 10: displayWaitingQueues(); break;
                case 0:
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("1. Register patient");
        System.out.println("2. Add patient to regular consultation queue");
        System.out.println("3. Add patient to emergency queue");
        System.out.println("4. Serve next emergency patient");
        System.out.println("5. Serve next regular patient");
        System.out.println("6. Search patient by ID");
        System.out.println("7. Update patient details");
        System.out.println("8. Remove patient record");
        System.out.println("9. Display all patient records");
        System.out.println("10. Display waiting queues");
        System.out.println("0. Exit");
    }

    private void registerPatient() {
        System.out.println("\n--- Register Patient ---");
        String name = readNonEmpty("Enter patient name: ");
        int age = readIntInRange("Enter age (0-130): ", 0, 130);
        String condition = readNonEmpty("Enter condition/reason for visit: ");

        Patient patient = new Patient(nextId++, name, age, condition, 0);
        patientRecords.add(patient);
        System.out.println("Patient registered successfully. Patient ID: " + patient.getId());
        System.out.println("Use option 2 for regular consultation or option 3 for an emergency.");
    }

    private void addToRegularQueue() {
        Patient patient = findPatientById(readInt("Enter patient ID: "));
        if (patient == null) {
            System.out.println("Patient record not found. Register the patient first.");
            return;
        }
        if (isWaiting(patient)) {
            System.out.println("This patient is already in a waiting queue.");
            return;
        }
        patient.setEmergencyPriority(0);
        regularQueue.offer(patient);
        System.out.println(patient.getName() + " added to the regular queue.");
    }

    private void addToEmergencyQueue() {
        Patient patient = findPatientById(readInt("Enter patient ID: "));
        if (patient == null) {
            System.out.println("Patient record not found. Register the patient first.");
            return;
        }
        if (isWaiting(patient)) {
            System.out.println("This patient is already in a waiting queue.");
            return;
        }
        int priority = readIntInRange("Enter emergency priority (1 = highest, 5 = lowest): ", 1, 5);
        patient.setEmergencyPriority(priority);
        emergencyQueue.offer(patient);
        System.out.println(patient.getName() + " added to the emergency queue with priority " + priority + ".");
    }

    private void serveEmergencyPatient() {
        Patient patient = emergencyQueue.poll();
        if (patient == null) {
            System.out.println("No emergency patients are waiting.");
            return;
        }
        patient.setEmergencyPriority(0);
        System.out.println("Serving emergency patient: " + patient);
    }

    private void serveRegularPatient() {
        Patient patient = regularQueue.poll();
        if (patient == null) {
            System.out.println("No regular patients are waiting.");
            return;
        }
        System.out.println("Serving regular patient: " + patient);
    }

    private void searchPatient() {
        int id = readInt("Enter patient ID to search: ");
        Patient patient = findPatientById(id);
        if (patient == null) {
            System.out.println("Patient record not found.");
        } else {
            System.out.println("Patient found:\n" + patient);
        }
    }

    private void updatePatient() {
        int id = readInt("Enter patient ID to update: ");
        Patient patient = findPatientById(id);
        if (patient == null) {
            System.out.println("Patient record not found.");
            return;
        }
        System.out.println("Current details: " + patient);
        String name = readNonEmpty("Enter new name: ");
        int age = readIntInRange("Enter new age (0-130): ", 0, 130);
        String condition = readNonEmpty("Enter new condition/reason: ");
        patient.update(name, age, condition);
        System.out.println("Patient details updated successfully.");
    }

    private void removePatient() {
        int id = readInt("Enter patient ID to remove: ");
        Patient patient = findPatientById(id);
        if (patient == null) {
            System.out.println("Patient record not found.");
            return;
        }
        patientRecords.remove(patient);
        regularQueue.remove(patient);
        emergencyQueue.remove(patient);
        System.out.println("Patient record and any waiting-queue entry removed.");
    }

    private void displayAllPatients() {
        System.out.println("\n--- All Patient Records ---");
        if (patientRecords.isEmpty()) {
            System.out.println("No patient records available.");
            return;
        }
        for (Patient patient : patientRecords) {
            System.out.println(patient);
        }
    }

    private void displayWaitingQueues() {
        System.out.println("\n--- Regular Queue (FIFO) ---");
        if (regularQueue.isEmpty()) {
            System.out.println("No regular patients waiting.");
        } else {
            for (Patient patient : regularQueue) {
                System.out.println(patient);
            }
        }

        System.out.println("\n--- Emergency Queue (highest priority first) ---");
        if (emergencyQueue.isEmpty()) {
            System.out.println("No emergency patients waiting.");
        } else {
            // PriorityQueue iteration is not guaranteed to be sorted, so sort a copy for display.
            List<Patient> ordered = new ArrayList<>(emergencyQueue);
            ordered.sort(Comparator.comparingInt(Patient::getEmergencyPriority)
                                   .thenComparingInt(Patient::getId));
            for (Patient patient : ordered) {
                System.out.println(patient);
            }
        }
    }

    private Patient findPatientById(int id) {
        for (Patient patient : patientRecords) {
            if (patient.getId() == id) {
                return patient;
            }
        }
        return null;
    }

    private boolean isWaiting(Patient patient) {
        return regularQueue.contains(patient) || emergencyQueue.contains(patient);
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Enter a value between " + min + " and " + max + ".");
        }
    }
}
