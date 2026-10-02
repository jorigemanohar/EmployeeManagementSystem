import { useEffect, useState } from "react";
import api from "./services/api";

import Login from "./components/Login";

import AdminDashboard from "./components/AdminDashboard";
import AttendanceSummary from "./components/AttendanceSummary";
import LeaveSummary from "./components/LeaveSummary";
import DepartmentSummary from "./components/DepartmentSummary";
import PayrollSummary from "./components/PayrollSummary";

import AdminNavigation from "./components/AdminNavigation";
import EmployeeNavigation from "./components/EmployeeNavigation";
import EmployeeAccountForm from "./components/EmployeeAccountForm";

import EmployeeProfile from "./components/EmployeeProfile";
import EmployeeAttendance from "./components/EmployeeAttendance";
import EmployeeLeave from "./components/EmployeeLeave";
import EmployeePayroll from "./components/EmployeePayroll";

import "./App.css";

function App() {
  /* =========================================================
     AUTHENTICATION
  ========================================================= */

  const [token, setToken] = useState(
    localStorage.getItem("token")
  );

  const [role, setRole] = useState(null);
  const [loadingAuth, setLoadingAuth] = useState(true);

  /* =========================================================
     NAVIGATION
  ========================================================= */

  const [adminSection, setAdminSection] =
    useState("dashboard");

  const [employeeSection, setEmployeeSection] =
    useState("profile");

  /* =========================================================
     ADMIN DATA
  ========================================================= */

  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [leaves, setLeaves] = useState([]);
  const [payrolls, setPayrolls] = useState([]);

  /* =========================================================
     SEARCH
  ========================================================= */

  const [employeeSearch, setEmployeeSearch] =
    useState("");

  const [departmentSearch, setDepartmentSearch] =
    useState("");

  /* =========================================================
     EMPLOYEE FORM
  ========================================================= */

  const [employeeForm, setEmployeeForm] = useState({
    employeeCode: "",
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    designation: "",
    departmentId: "",
    salary: "",
    joiningDate: "",
  });

  const [editingEmployeeId, setEditingEmployeeId] =
    useState(null);

  /* =========================================================
     DEPARTMENT FORM
  ========================================================= */

  const [departmentForm, setDepartmentForm] = useState({
    departmentName: "",
    description: "",
  });

  const [
    editingDepartmentId,
    setEditingDepartmentId,
  ] = useState(null);

  /* =========================================================
     ATTENDANCE FORM
  ========================================================= */

  const [attendanceForm, setAttendanceForm] =
    useState({
      employeeId: "",
      attendanceDate: "",
      status: "PRESENT",
    });

  const [
    editingAttendanceId,
    setEditingAttendanceId,
  ] = useState(null);

  /* =========================================================
     LEAVE FORM
  ========================================================= */

  const [leaveForm, setLeaveForm] = useState({
    employeeId: "",
    leaveType: "SICK",
    startDate: "",
    endDate: "",
    reason: "",
    status: "PENDING",
  });

  const [editingLeaveId, setEditingLeaveId] =
    useState(null);

  /* =========================================================
     PAYROLL FORM
  ========================================================= */

  const [payrollForm, setPayrollForm] = useState({
    employeeId: "",
    month: "",
    basicSalary: "",
    allowances: "",
    deductions: "",
    paymentStatus: "PENDING",
  });

  const [editingPayrollId, setEditingPayrollId] =
    useState(null);

  /* =========================================================
     GENERAL MESSAGES
  ========================================================= */

  const [adminMessage, setAdminMessage] =
    useState("");

  const [adminError, setAdminError] =
    useState("");

  /* =========================================================
     JWT ROLE
  ========================================================= */

  const getRoleFromToken = (jwtToken) => {
    try {
      if (!jwtToken) {
        return null;
      }

      const parts = jwtToken.split(".");

      if (parts.length !== 3) {
        return null;
      }

      const payload = JSON.parse(
        atob(
          parts[1]
            .replace(/-/g, "+")
            .replace(/_/g, "/")
        )
      );

      const tokenRole = payload.role;

      if (!tokenRole) {
        return null;
      }

      return tokenRole.startsWith("ROLE_")
        ? tokenRole.replace("ROLE_", "")
        : tokenRole;
    } catch (error) {
      console.error(
        "Unable to decode JWT:",
        error
      );

      return null;
    }
  };

  /* =========================================================
     INITIAL AUTH CHECK
  ========================================================= */

  useEffect(() => {
    const storedToken =
      localStorage.getItem("token");

    if (!storedToken) {
      setLoadingAuth(false);
      return;
    }

    const storedRole =
      getRoleFromToken(storedToken);

    if (!storedRole) {
      localStorage.removeItem("token");
      setToken(null);
      setRole(null);
      setLoadingAuth(false);
      return;
    }

    setToken(storedToken);
    setRole(storedRole);
    setLoadingAuth(false);
  }, []);

  /* =========================================================
     LOGIN
  ========================================================= */

  const handleLogin = (newToken) => {
    localStorage.setItem(
      "token",
      newToken
    );

    const newRole =
      getRoleFromToken(newToken);

    setToken(newToken);
    setRole(newRole);

    if (newRole === "ADMIN") {
      setAdminSection("dashboard");
    }

    if (newRole === "EMPLOYEE") {
      setEmployeeSection("profile");
    }
  };

  /* =========================================================
     LOGOUT
  ========================================================= */

  const handleLogout = () => {
    localStorage.removeItem("token");

    setToken(null);
    setRole(null);

    setAdminSection("dashboard");
    setEmployeeSection("profile");

    setEmployees([]);
    setDepartments([]);
    setAttendance([]);
    setLeaves([]);
    setPayrolls([]);

    resetEmployeeForm();
    resetDepartmentForm();
    resetAttendanceForm();
    resetLeaveForm();
    resetPayrollForm();
  };

  /* =========================================================
     LOAD EMPLOYEES
  ========================================================= */

  const loadEmployees = async () => {
    try {
      const response =
        await api.get("/employees");

      setEmployees(response.data);
    } catch (error) {
      console.error(
        "Error loading employees:",
        error
      );
    }
  };

  /* =========================================================
     LOAD DEPARTMENTS
  ========================================================= */

  const loadDepartments = async () => {
    try {
      const response =
        await api.get("/departments");

      setDepartments(response.data);
    } catch (error) {
      console.error(
        "Error loading departments:",
        error
      );
    }
  };

  /* =========================================================
     LOAD ATTENDANCE
  ========================================================= */

  const loadAttendance = async () => {
    try {
      const response =
        await api.get("/attendance");

      setAttendance(response.data);
    } catch (error) {
      console.error(
        "Error loading attendance:",
        error
      );
    }
  };

  /* =========================================================
     LOAD LEAVES
  ========================================================= */

  const loadLeaves = async () => {
    try {
      const response =
        await api.get("/leaves");

      setLeaves(response.data);
    } catch (error) {
      console.error(
        "Error loading leaves:",
        error
      );
    }
  };

  /* =========================================================
     LOAD PAYROLL
  ========================================================= */

  const loadPayrolls = async () => {
    try {
      const response =
        await api.get("/payroll");

      setPayrolls(response.data);
    } catch (error) {
      console.error(
        "Error loading payroll:",
        error
      );
    }
  };

 /* =========================================================
   LOAD ALL ADMIN DATA
========================================================= */

const loadAllAdminData = async () => {
  await Promise.all([
    loadEmployees(),
    loadDepartments(),
    loadAttendance(),
    loadLeaves(),
    loadPayrolls(),
  ]);
};

/* =========================================================
   LOAD ADMIN DATA AFTER LOGIN
========================================================= */

useEffect(() => {
  const loadAdminData = async () => {
    if (token && role === "ADMIN") {
      await Promise.all([
        loadEmployees(),
        loadDepartments(),
        loadAttendance(),
        loadLeaves(),
        loadPayrolls(),
      ]);
    }
  };

  loadAdminData();
}, [token, role]);
  /* =========================================================
     EMPLOYEE FORM
  ========================================================= */

  const resetEmployeeForm = () => {
    setEmployeeForm({
      employeeCode: "",
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      designation: "",
      departmentId: "",
      salary: "",
      joiningDate: "",
    });

    setEditingEmployeeId(null);
  };

  const handleEmployeeChange = (e) => {
    const { name, value } = e.target;

    setEmployeeForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleEmployeeSubmit = async (e) => {
    e.preventDefault();

    setAdminMessage("");
    setAdminError("");

    try {
      const employeeData = {
        employeeCode:
          employeeForm.employeeCode.trim(),

        firstName:
          employeeForm.firstName.trim(),

        lastName:
          employeeForm.lastName.trim(),

        email:
          employeeForm.email.trim(),

        phone:
          employeeForm.phone.trim(),

        designation:
          employeeForm.designation.trim(),

        department:
          employeeForm.departmentId
            ? {
                id: Number(
                  employeeForm.departmentId
                ),
              }
            : null,

        salary:
          employeeForm.salary
            ? Number(
                employeeForm.salary
              )
            : 0,

        joiningDate:
          employeeForm.joiningDate ||
          null,
      };

      if (editingEmployeeId) {
        await api.put(
          `/employees/${editingEmployeeId}`,
          employeeData
        );

        setAdminMessage(
          "Employee updated successfully."
        );
      } else {
        await api.post(
          "/employees",
          employeeData
        );

        setAdminMessage(
          "Employee added successfully."
        );
      }

      resetEmployeeForm();

      await loadEmployees();
    } catch (error) {
      console.error(
        "Employee save error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to save employee."
      );
    }
  };

  const handleEditEmployee = (employee) => {
    setAdminMessage("");
    setAdminError("");

    setEditingEmployeeId(
      employee.id
    );

    setEmployeeForm({
      employeeCode:
        employee.employeeCode || "",

      firstName:
        employee.firstName || "",

      lastName:
        employee.lastName || "",

      email:
        employee.email || "",

      phone:
        employee.phone || "",

      designation:
        employee.designation || "",

      departmentId:
        employee.department?.id
          ? String(
              employee.department.id
            )
          : "",

      salary:
        employee.salary !== null &&
        employee.salary !== undefined
          ? String(
              employee.salary
            )
          : "",

      joiningDate:
        employee.joiningDate || "",
    });

    setAdminSection("employees");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeleteEmployee = async (
    id
  ) => {
    const confirmed =
      window.confirm(
        "Are you sure you want to delete this employee?"
      );

    if (!confirmed) {
      return;
    }

    setAdminMessage("");
    setAdminError("");

    try {
      await api.delete(
        `/employees/${id}`
      );

      setAdminMessage(
        "Employee deleted successfully."
      );

      if (
        editingEmployeeId === id
      ) {
        resetEmployeeForm();
      }

      await loadAllAdminData();
    } catch (error) {
      console.error(
        "Employee delete error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to delete employee."
      );
    }
  };

  /* =========================================================
     DEPARTMENT FORM
  ========================================================= */

  const resetDepartmentForm = () => {
    setDepartmentForm({
      departmentName: "",
      description: "",
    });

    setEditingDepartmentId(null);
  };

  const handleDepartmentChange = (e) => {
    const { name, value } = e.target;

    setDepartmentForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDepartmentSubmit =
    async (e) => {
      e.preventDefault();

      setAdminMessage("");
      setAdminError("");

      try {
        const departmentData = {
          departmentName:
            departmentForm.departmentName.trim(),

          description:
            departmentForm.description.trim(),
        };

        if (editingDepartmentId) {
          await api.put(
            `/departments/${editingDepartmentId}`,
            departmentData
          );

          setAdminMessage(
            "Department updated successfully."
          );
        } else {
          await api.post(
            "/departments",
            departmentData
          );

          setAdminMessage(
            "Department added successfully."
          );
        }

        resetDepartmentForm();

        await Promise.all([
          loadDepartments(),
          loadEmployees(),
        ]);
      } catch (error) {
        console.error(
          "Department save error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to save department."
        );
      }
    };

  const handleEditDepartment = (
    department
  ) => {
    setEditingDepartmentId(
      department.id
    );

    setDepartmentForm({
      departmentName:
        department.departmentName ||
        "",

      description:
        department.description || "",
    });

    setAdminSection("departments");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeleteDepartment =
    async (id) => {
      const confirmed =
        window.confirm(
          "Are you sure you want to delete this department?"
        );

      if (!confirmed) {
        return;
      }

      setAdminMessage("");
      setAdminError("");

      try {
        await api.delete(
          `/departments/${id}`
        );

        setAdminMessage(
          "Department deleted successfully."
        );

        if (
          editingDepartmentId === id
        ) {
          resetDepartmentForm();
        }

        await Promise.all([
          loadDepartments(),
          loadEmployees(),
        ]);
      } catch (error) {
        console.error(
          "Department delete error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to delete department."
        );
      }
    };

  /* =========================================================
     ATTENDANCE FORM
  ========================================================= */

  const resetAttendanceForm = () => {
    setAttendanceForm({
      employeeId: "",
      attendanceDate: "",
      status: "PRESENT",
    });

    setEditingAttendanceId(null);
  };

  const handleAttendanceChange = (
    e
  ) => {
    const { name, value } = e.target;

    setAttendanceForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAttendanceSubmit =
    async (e) => {
      e.preventDefault();

      setAdminMessage("");
      setAdminError("");

      try {
        const attendanceData = {
          employee: {
            id: Number(
              attendanceForm.employeeId
            ),
          },

          attendanceDate:
            attendanceForm.attendanceDate,

          status:
            attendanceForm.status,
        };

        if (editingAttendanceId) {
          await api.put(
            `/attendance/${editingAttendanceId}`,
            attendanceData
          );

          setAdminMessage(
            "Attendance updated successfully."
          );
        } else {
          await api.post(
            "/attendance",
            attendanceData
          );

          setAdminMessage(
            "Attendance added successfully."
          );
        }

        resetAttendanceForm();

        await loadAttendance();
      } catch (error) {
        console.error(
          "Attendance save error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to save attendance."
        );
      }
    };

  const handleEditAttendance = (
    record
  ) => {
    setEditingAttendanceId(
      record.id
    );

    setAttendanceForm({
      employeeId:
        record.employee?.id
          ? String(
              record.employee.id
            )
          : "",

      attendanceDate:
        record.attendanceDate || "",

      status:
        record.status || "PRESENT",
    });

    setAdminSection("attendance");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeleteAttendance =
    async (id) => {
      const confirmed =
        window.confirm(
          "Are you sure you want to delete this attendance record?"
        );

      if (!confirmed) {
        return;
      }

      setAdminMessage("");
      setAdminError("");

      try {
        await api.delete(
          `/attendance/${id}`
        );

        setAdminMessage(
          "Attendance deleted successfully."
        );

        if (
          editingAttendanceId === id
        ) {
          resetAttendanceForm();
        }

        await loadAttendance();
      } catch (error) {
        console.error(
          "Attendance delete error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to delete attendance."
        );
      }
    };

  /* =========================================================
     LEAVE FORM
  ========================================================= */

  const resetLeaveForm = () => {
    setLeaveForm({
      employeeId: "",
      leaveType: "SICK",
      startDate: "",
      endDate: "",
      reason: "",
      status: "PENDING",
    });

    setEditingLeaveId(null);
  };

  const handleLeaveChange = (e) => {
    const { name, value } = e.target;

    setLeaveForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleLeaveSubmit = async (e) => {
    e.preventDefault();

    setAdminMessage("");
    setAdminError("");

    try {
      const leaveData = {
        employee: {
          id: Number(
            leaveForm.employeeId
          ),
        },

        leaveType:
          leaveForm.leaveType,

        startDate:
          leaveForm.startDate,

        endDate:
          leaveForm.endDate,

        reason:
          leaveForm.reason.trim(),

        status:
          leaveForm.status,
      };

      if (editingLeaveId) {
        await api.put(
          `/leaves/${editingLeaveId}`,
          leaveData
        );

        setAdminMessage(
          "Leave updated successfully."
        );
      } else {
        await api.post(
          "/leaves",
          leaveData
        );

        setAdminMessage(
          "Leave added successfully."
        );
      }

      resetLeaveForm();

      await loadLeaves();
    } catch (error) {
      console.error(
        "Leave save error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to save leave."
      );
    }
  };

  const handleEditLeave = (leave) => {
    setEditingLeaveId(
      leave.id
    );

    setLeaveForm({
      employeeId:
        leave.employee?.id
          ? String(
              leave.employee.id
            )
          : "",

      leaveType:
        leave.leaveType || "SICK",

      startDate:
        leave.startDate || "",

      endDate:
        leave.endDate || "",

      reason:
        leave.reason || "",

      status:
        leave.status || "PENDING",
    });

    setAdminSection("leaves");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeleteLeave = async (
    id
  ) => {
    const confirmed =
      window.confirm(
        "Are you sure you want to delete this leave record?"
      );

    if (!confirmed) {
      return;
    }

    setAdminMessage("");
    setAdminError("");

    try {
      await api.delete(
        `/leaves/${id}`
      );

      setAdminMessage(
        "Leave deleted successfully."
      );

      if (
        editingLeaveId === id
      ) {
        resetLeaveForm();
      }

      await loadLeaves();
    } catch (error) {
      console.error(
        "Leave delete error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to delete leave."
      );
    }
  };

  /* =========================================================
     APPROVE LEAVE
  ========================================================= */

  const handleApproveLeave = async (
    leave
  ) => {
    const confirmed =
      window.confirm(
        `Approve leave request for ${
          leave.employee?.employeeCode ||
          "employee"
        }?`
      );

    if (!confirmed) {
      return;
    }

    setAdminMessage("");
    setAdminError("");

    try {
      const leaveData = {
        employee: {
          id: Number(
            leave.employee?.id
          ),
        },

        leaveType:
          leave.leaveType,

        startDate:
          leave.startDate,

        endDate:
          leave.endDate,

        reason:
          leave.reason || "",

        status: "APPROVED",
      };

      await api.put(
        `/leaves/${leave.id}`,
        leaveData
      );

      setAdminMessage(
        "Leave approved successfully."
      );

      await loadLeaves();
    } catch (error) {
      console.error(
        "Leave approval error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to approve leave."
      );
    }
  };

  /* =========================================================
     REJECT LEAVE
  ========================================================= */

  const handleRejectLeave = async (
    leave
  ) => {
    const confirmed =
      window.confirm(
        `Reject leave request for ${
          leave.employee?.employeeCode ||
          "employee"
        }?`
      );

    if (!confirmed) {
      return;
    }

    setAdminMessage("");
    setAdminError("");

    try {
      const leaveData = {
        employee: {
          id: Number(
            leave.employee?.id
          ),
        },

        leaveType:
          leave.leaveType,

        startDate:
          leave.startDate,

        endDate:
          leave.endDate,

        reason:
          leave.reason || "",

        status: "REJECTED",
      };

      await api.put(
        `/leaves/${leave.id}`,
        leaveData
      );

      setAdminMessage(
        "Leave rejected successfully."
      );

      await loadLeaves();
    } catch (error) {
      console.error(
        "Leave rejection error:",
        error
      );

      setAdminError(
        error.response?.data ||
          "Unable to reject leave."
      );
    }
  };

  /* =========================================================
     PAYROLL FORM
  ========================================================= */

  const resetPayrollForm = () => {
    setPayrollForm({
      employeeId: "",
      month: "",
      basicSalary: "",
      allowances: "",
      deductions: "",
      paymentStatus: "PENDING",
    });

    setEditingPayrollId(null);
  };

  const handlePayrollChange = (e) => {
    const { name, value } = e.target;

    setPayrollForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePayrollSubmit =
    async (e) => {
      e.preventDefault();

      setAdminMessage("");
      setAdminError("");

      try {
        const payrollData = {
          employee: {
            id: Number(
              payrollForm.employeeId
            ),
          },

          month:
            payrollForm.month,

          basicSalary:
            Number(
              payrollForm.basicSalary ||
                0
            ),

          allowances:
            Number(
              payrollForm.allowances ||
                0
            ),

          deductions:
            Number(
              payrollForm.deductions ||
                0
            ),

          paymentStatus:
            payrollForm.paymentStatus,
        };

        if (editingPayrollId) {
          await api.put(
            `/payroll/${editingPayrollId}`,
            payrollData
          );

          setAdminMessage(
            "Payroll updated successfully."
          );
        } else {
          await api.post(
            "/payroll",
            payrollData
          );

          setAdminMessage(
            "Payroll added successfully."
          );
        }

        resetPayrollForm();

        await loadPayrolls();
      } catch (error) {
        console.error(
          "Payroll save error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to save payroll."
        );
      }
    };

  const handleEditPayroll = (
    payroll
  ) => {
    setEditingPayrollId(
      payroll.id
    );

    setPayrollForm({
      employeeId:
        payroll.employee?.id
          ? String(
              payroll.employee.id
            )
          : "",

      month:
        payroll.month || "",

      basicSalary:
        payroll.basicSalary !== null &&
        payroll.basicSalary !== undefined
          ? String(
              payroll.basicSalary
            )
          : "",

      allowances:
        payroll.allowances !== null &&
        payroll.allowances !== undefined
          ? String(
              payroll.allowances
            )
          : "",

      deductions:
        payroll.deductions !== null &&
        payroll.deductions !== undefined
          ? String(
              payroll.deductions
            )
          : "",

      paymentStatus:
        payroll.paymentStatus ||
        "PENDING",
    });

    setAdminSection("payroll");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeletePayroll =
    async (id) => {
      const confirmed =
        window.confirm(
          "Are you sure you want to delete this payroll record?"
        );

      if (!confirmed) {
        return;
      }

      setAdminMessage("");
      setAdminError("");

      try {
        await api.delete(
          `/payroll/${id}`
        );

        setAdminMessage(
          "Payroll deleted successfully."
        );

        if (
          editingPayrollId === id
        ) {
          resetPayrollForm();
        }

        await loadPayrolls();
      } catch (error) {
        console.error(
          "Payroll delete error:",
          error
        );

        setAdminError(
          error.response?.data ||
            "Unable to delete payroll."
        );
      }
    };

  /* =========================================================
     SEARCH EMPLOYEES
  ========================================================= */

  const filteredEmployees =
    employees.filter(
      (employee) => {
        const search =
          employeeSearch
            .toLowerCase()
            .trim();

        if (!search) {
          return true;
        }

        return (
          employee.employeeCode
            ?.toLowerCase()
            .includes(search) ||

          employee.firstName
            ?.toLowerCase()
            .includes(search) ||

          employee.lastName
            ?.toLowerCase()
            .includes(search) ||

          employee.email
            ?.toLowerCase()
            .includes(search) ||

          employee.designation
            ?.toLowerCase()
            .includes(search)
        );
      }
    );

  /* =========================================================
     SEARCH DEPARTMENTS
  ========================================================= */

  const filteredDepartments =
    departments.filter(
      (department) => {
        const search =
          departmentSearch
            .toLowerCase()
            .trim();

        if (!search) {
          return true;
        }

        return (
          department.departmentName
            ?.toLowerCase()
            .includes(search) ||

          department.description
            ?.toLowerCase()
            .includes(search)
        );
      }
    );

  /* =========================================================
     LOADING
  ========================================================= */

  if (loadingAuth) {
    return (
      <div className="loading-screen">

        <h2>
          Loading Employee Management
          System...
        </h2>

      </div>
    );
  }

  /* =========================================================
     LOGIN
  ========================================================= */

  if (!token || !role) {
    return (
      <Login
        onLogin={handleLogin}
      />
    );
  }

  /* =========================================================
     EMPLOYEE DASHBOARD
  ========================================================= */

  if (role === "EMPLOYEE") {
    return (
      <div className="employee-dashboard">

        <header className="dashboard-header">

          <div>

            <h1>
              Employee Dashboard
            </h1>

            <p>
              Welcome to the Employee
              Management System
            </p>

          </div>

          <button
            className="logout-button"
            onClick={handleLogout}
          >
            Logout
          </button>

        </header>

        <EmployeeNavigation
          activeSection={
            employeeSection
          }
          onNavigate={
            setEmployeeSection
          }
        />

        <main className="dashboard-content">

          {employeeSection ===
            "profile" && (
            <EmployeeProfile />
          )}

          {employeeSection ===
            "attendance" && (
            <EmployeeAttendance />
          )}

          {employeeSection ===
            "leave" && (
            <EmployeeLeave />
          )}

          {employeeSection ===
            "payroll" && (
            <EmployeePayroll />
          )}

        </main>

      </div>
    );
  }

  /* =========================================================
     ADMIN DASHBOARD
  ========================================================= */

  if (role === "ADMIN") {
    return (
      <div className="admin-dashboard">

        <header className="dashboard-header">

          <div>

            <h1>
              Admin Dashboard
            </h1>

            <p>
              Employee Management &
              Payroll System
            </p>

          </div>

          <button
            className="logout-button"
            onClick={handleLogout}
          >
            Logout
          </button>

        </header>

        <main className="dashboard-content">

          <AdminNavigation
            activeSection={
              adminSection
            }
            onNavigate={(section) => {

              setAdminSection(
                section
              );

              setAdminMessage("");
              setAdminError("");

            }}
          />

          {adminMessage && (
            <div className="success-message">
              {adminMessage}
            </div>
          )}

          {adminError && (
            <div className="error-message">
              {adminError}
            </div>
          )}

          {/* =================================================
              DASHBOARD
          ================================================= */}

          {adminSection ===
            "dashboard" && (
            <>

              <AdminDashboard
                employees={
                  employees
                }
                departments={
                  departments
                }
                attendance={
                  attendance
                }
                leaves={
                  leaves
                }
                payrolls={
                  payrolls
                }
              />

              <section className="dashboard-section">

                <AttendanceSummary
                  attendance={
                    attendance
                  }
                />

              </section>

              <section className="dashboard-section">

                <LeaveSummary
                  leaves={
                    leaves
                  }
                />

              </section>

              <section className="dashboard-section">

                <DepartmentSummary
                  departments={
                    departments
                  }
                  employees={
                    employees
                  }
                />

              </section>

              <section className="dashboard-section">

                <PayrollSummary
                  payrolls={
                    payrolls
                  }
                />

              </section>

            </>
          )}

          {/* =================================================
              EMPLOYEES
          ================================================= */}

          {adminSection ===
            "employees" && (
            <>

              <section className="form-section">

                <h2>
                  {editingEmployeeId
                    ? "Update Employee"
                    : "Add Employee"}
                </h2>

                <form
                  onSubmit={
                    handleEmployeeSubmit
                  }
                  className="data-form"
                >

                  <div className="form-grid">

                    <div className="form-group">

                      <label>
                        Employee Code
                      </label>

                      <input
                        type="text"
                        name="employeeCode"
                        value={
                          employeeForm.employeeCode
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="EMP007"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        First Name
                      </label>

                      <input
                        type="text"
                        name="firstName"
                        value={
                          employeeForm.firstName
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="First Name"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Last Name
                      </label>

                      <input
                        type="text"
                        name="lastName"
                        value={
                          employeeForm.lastName
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="Last Name"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Email
                      </label>

                      <input
                        type="email"
                        name="email"
                        value={
                          employeeForm.email
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="employee@email.com"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Phone
                      </label>

                      <input
                        type="text"
                        name="phone"
                        value={
                          employeeForm.phone
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="9876543210"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Designation
                      </label>

                      <input
                        type="text"
                        name="designation"
                        value={
                          employeeForm.designation
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="Software Developer"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Department
                      </label>

                      <select
                        name="departmentId"
                        value={
                          employeeForm.departmentId
                        }
                        onChange={
                          handleEmployeeChange
                        }
                      >

                        <option value="">
                          Select Department
                        </option>

                        {departments.map(
                          (
                            department
                          ) => (

                            <option
                              key={
                                department.id
                              }
                              value={
                                department.id
                              }
                            >
                              {
                                department.departmentName
                              }
                            </option>

                          )
                        )}

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Salary
                      </label>

                      <input
                        type="number"
                        name="salary"
                        value={
                          employeeForm.salary
                        }
                        onChange={
                          handleEmployeeChange
                        }
                        placeholder="35000"
                        min="0"
                        step="0.01"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Joining Date
                      </label>

                      <input
                        type="date"
                        name="joiningDate"
                        value={
                          employeeForm.joiningDate
                        }
                        onChange={
                          handleEmployeeChange
                        }
                      />

                    </div>

                  </div>

                  <div className="form-actions">

                    <button type="submit">

                      {editingEmployeeId
                        ? "Update Employee"
                        : "Add Employee"}

                    </button>

                    {editingEmployeeId && (

                      <button
                        type="button"
                        onClick={
                          resetEmployeeForm
                        }
                      >
                        Cancel Edit
                      </button>

                    )}

                  </div>

                </form>

              </section>

              <section className="form-section">

                <EmployeeAccountForm
                  employees={
                    employees
                  }
                  onAccountCreated={
                    loadEmployees
                  }
                />

              </section>

              <section className="data-section">

                <div className="section-header">

                  <h2>
                    Employees
                  </h2>

                  <input
                    type="text"
                    value={
                      employeeSearch
                    }
                    onChange={(e) =>
                      setEmployeeSearch(
                        e.target.value
                      )
                    }
                    placeholder="Search employees..."
                    className="search-input"
                  />

                </div>

                {filteredEmployees.length ===
                0 ? (

                  <p>
                    No employees found.
                  </p>

                ) : (

                  <div className="table-container">

                    <table>

                      <thead>

                        <tr>

                          <th>ID</th>
                          <th>Code</th>
                          <th>Name</th>
                          <th>Email</th>
                          <th>Phone</th>
                          <th>Designation</th>
                          <th>Department</th>
                          <th>Salary</th>
                          <th>Joining Date</th>
                          <th>Account Status</th>
                          <th>Actions</th>

                        </tr>

                      </thead>

                      <tbody>

                        {filteredEmployees.map(
                          (employee) => (

                            <tr
                              key={
                                employee.id
                              }
                            >

                              <td>
                                {
                                  employee.id
                                }
                              </td>

                              <td>
                                {
                                  employee.employeeCode
                                }
                              </td>

                              <td>
                                {
                                  employee.firstName
                                }{" "}
                                {
                                  employee.lastName
                                }
                              </td>

                              <td>
                                {
                                  employee.email
                                }
                              </td>

                              <td>
                                {
                                  employee.phone ||
                                  "-"
                                }
                              </td>

                              <td>
                                {
                                  employee.designation ||
                                  "-"
                                }
                              </td>

                              <td>
                                {
                                  employee
                                    .department
                                    ?.departmentName ||
                                  "-"
                                }
                              </td>

                              <td>
                                ₹
                                {Number(
                                  employee.salary ||
                                    0
                                ).toLocaleString(
                                  "en-IN"
                                )}
                              </td>

                              <td>
                                {
                                  employee.joiningDate ||
                                  "-"
                                }
                              </td>

                              <td>

                                {employee.accountCreated ? (

                                  <span className="account-status created">
                                    ✅ Created
                                  </span>

                                ) : (

                                  <span className="account-status not-created">
                                    ❌ Not Created
                                  </span>

                                )}

                              </td>

                              <td>

                                <div className="table-actions">

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleEditEmployee(
                                        employee
                                      )
                                    }
                                  >
                                    Edit
                                  </button>

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleDeleteEmployee(
                                        employee.id
                                      )
                                    }
                                  >
                                    Delete
                                  </button>

                                </div>

                              </td>

                            </tr>

                          )
                        )}

                      </tbody>

                    </table>

                  </div>

                )}

              </section>

            </>
          )}

          {/* =================================================
              DEPARTMENTS
          ================================================= */}

          {adminSection ===
            "departments" && (
            <>

              <section className="form-section">

                <h2>
                  {editingDepartmentId
                    ? "Update Department"
                    : "Add Department"}
                </h2>

                <form
                  onSubmit={
                    handleDepartmentSubmit
                  }
                  className="data-form"
                >

                  <div className="form-grid">

                    <div className="form-group">

                      <label>
                        Department Name
                      </label>

                      <input
                        type="text"
                        name="departmentName"
                        value={
                          departmentForm.departmentName
                        }
                        onChange={
                          handleDepartmentChange
                        }
                        placeholder="Information Technology"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Description
                      </label>

                      <input
                        type="text"
                        name="description"
                        value={
                          departmentForm.description
                        }
                        onChange={
                          handleDepartmentChange
                        }
                        placeholder="Department description"
                      />

                    </div>

                  </div>

                  <div className="form-actions">

                    <button type="submit">

                      {editingDepartmentId
                        ? "Update Department"
                        : "Add Department"}

                    </button>

                    {editingDepartmentId && (

                      <button
                        type="button"
                        onClick={
                          resetDepartmentForm
                        }
                      >
                        Cancel Edit
                      </button>

                    )}

                  </div>

                </form>

              </section>

              <section className="data-section">

                <div className="section-header">

                  <h2>
                    Departments
                  </h2>

                  <input
                    type="text"
                    value={
                      departmentSearch
                    }
                    onChange={(e) =>
                      setDepartmentSearch(
                        e.target.value
                      )
                    }
                    placeholder="Search departments..."
                    className="search-input"
                  />

                </div>

                {filteredDepartments.length ===
                0 ? (

                  <p>
                    No departments found.
                  </p>

                ) : (

                  <div className="table-container">

                    <table>

                      <thead>

                        <tr>
                          <th>ID</th>
                          <th>Department Name</th>
                          <th>Description</th>
                          <th>Employee Count</th>
                          <th>Actions</th>
                        </tr>

                      </thead>

                      <tbody>

                        {filteredDepartments.map(
                          (department) => {

                            const employeeCount =
                              employees.filter(
                                (employee) =>
                                  employee
                                    .department
                                    ?.id ===
                                  department.id
                              ).length;

                            return (

                              <tr
                                key={
                                  department.id
                                }
                              >

                                <td>
                                  {
                                    department.id
                                  }
                                </td>

                                <td>
                                  {
                                    department.departmentName
                                  }
                                </td>

                                <td>
                                  {
                                    department.description ||
                                    "-"
                                  }
                                </td>

                                <td>
                                  {
                                    employeeCount
                                  }
                                </td>

                                <td>

                                  <div className="table-actions">

                                    <button
                                      type="button"
                                      onClick={() =>
                                        handleEditDepartment(
                                          department
                                        )
                                      }
                                    >
                                      Edit
                                    </button>

                                    <button
                                      type="button"
                                      onClick={() =>
                                        handleDeleteDepartment(
                                          department.id
                                        )
                                      }
                                    >
                                      Delete
                                    </button>

                                  </div>

                                </td>

                              </tr>

                            );
                          }
                        )}

                      </tbody>

                    </table>

                  </div>

                )}

              </section>

            </>
          )}

          {/* =================================================
              ATTENDANCE
          ================================================= */}

          {adminSection ===
            "attendance" && (
            <>

              <section className="form-section">

                <h2>
                  {editingAttendanceId
                    ? "Update Attendance"
                    : "Add Attendance"}
                </h2>

                <form
                  onSubmit={
                    handleAttendanceSubmit
                  }
                  className="data-form"
                >

                  <div className="form-grid">

                    <div className="form-group">

                      <label>
                        Employee
                      </label>

                      <select
                        name="employeeId"
                        value={
                          attendanceForm.employeeId
                        }
                        onChange={
                          handleAttendanceChange
                        }
                        required
                      >

                        <option value="">
                          Select Employee
                        </option>

                        {employees.map(
                          (employee) => (

                            <option
                              key={
                                employee.id
                              }
                              value={
                                employee.id
                              }
                            >
                              {
                                employee.employeeCode
                              }{" "}
                              -{" "}
                              {
                                employee.firstName
                              }{" "}
                              {
                                employee.lastName
                              }
                            </option>

                          )
                        )}

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Attendance Date
                      </label>

                      <input
                        type="date"
                        name="attendanceDate"
                        value={
                          attendanceForm.attendanceDate
                        }
                        onChange={
                          handleAttendanceChange
                        }
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Status
                      </label>

                      <select
                        name="status"
                        value={
                          attendanceForm.status
                        }
                        onChange={
                          handleAttendanceChange
                        }
                        required
                      >

                        <option value="PRESENT">
                          PRESENT
                        </option>

                        <option value="ABSENT">
                          ABSENT
                        </option>

                        <option value="LEAVE">
                          LEAVE
                        </option>

                      </select>

                    </div>

                  </div>

                  <div className="form-actions">

                    <button type="submit">

                      {editingAttendanceId
                        ? "Update Attendance"
                        : "Add Attendance"}

                    </button>

                    {editingAttendanceId && (

                      <button
                        type="button"
                        onClick={
                          resetAttendanceForm
                        }
                      >
                        Cancel Edit
                      </button>

                    )}

                  </div>

                </form>

              </section>

              <section className="data-section">

                <h2>
                  Attendance Records
                </h2>

                {attendance.length ===
                0 ? (

                  <p>
                    No attendance records found.
                  </p>

                ) : (

                  <div className="table-container">

                    <table>

                      <thead>

                        <tr>
                          <th>ID</th>
                          <th>Employee</th>
                          <th>Date</th>
                          <th>Status</th>
                          <th>Actions</th>
                        </tr>

                      </thead>

                      <tbody>

                        {attendance.map(
                          (record) => (

                            <tr
                              key={
                                record.id
                              }
                            >

                              <td>
                                {
                                  record.id
                                }
                              </td>

                              <td>
                                {
                                  record.employee
                                    ?.employeeCode
                                }{" "}
                                -{" "}
                                {
                                  record.employee
                                    ?.firstName
                                }{" "}
                                {
                                  record.employee
                                    ?.lastName
                                }
                              </td>

                              <td>
                                {
                                  record.attendanceDate
                                }
                              </td>

                              <td>
                                {
                                  record.status
                                }
                              </td>

                              <td>

                                <div className="table-actions">

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleEditAttendance(
                                        record
                                      )
                                    }
                                  >
                                    Edit
                                  </button>

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleDeleteAttendance(
                                        record.id
                                      )
                                    }
                                  >
                                    Delete
                                  </button>

                                </div>

                              </td>

                            </tr>

                          )
                        )}

                      </tbody>

                    </table>

                  </div>

                )}

              </section>

            </>
          )}

          {/* =================================================
              LEAVES
          ================================================= */}

          {adminSection ===
            "leaves" && (
            <>

              <section className="form-section">

                <h2>
                  {editingLeaveId
                    ? "Update Leave"
                    : "Add Leave"}
                </h2>

                <form
                  onSubmit={
                    handleLeaveSubmit
                  }
                  className="data-form"
                >

                  <div className="form-grid">

                    <div className="form-group">

                      <label>
                        Employee
                      </label>

                      <select
                        name="employeeId"
                        value={
                          leaveForm.employeeId
                        }
                        onChange={
                          handleLeaveChange
                        }
                        required
                      >

                        <option value="">
                          Select Employee
                        </option>

                        {employees.map(
                          (employee) => (

                            <option
                              key={
                                employee.id
                              }
                              value={
                                employee.id
                              }
                            >
                              {
                                employee.employeeCode
                              }{" "}
                              -{" "}
                              {
                                employee.firstName
                              }{" "}
                              {
                                employee.lastName
                              }
                            </option>

                          )
                        )}

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Leave Type
                      </label>

                      <select
                        name="leaveType"
                        value={
                          leaveForm.leaveType
                        }
                        onChange={
                          handleLeaveChange
                        }
                        required
                      >

                        <option value="SICK">
                          SICK
                        </option>

                        <option value="CASUAL">
                          CASUAL
                        </option>

                        <option value="EARNED">
                          EARNED
                        </option>

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Start Date
                      </label>

                      <input
                        type="date"
                        name="startDate"
                        value={
                          leaveForm.startDate
                        }
                        onChange={
                          handleLeaveChange
                        }
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        End Date
                      </label>

                      <input
                        type="date"
                        name="endDate"
                        value={
                          leaveForm.endDate
                        }
                        onChange={
                          handleLeaveChange
                        }
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Status
                      </label>

                      <select
                        name="status"
                        value={
                          leaveForm.status
                        }
                        onChange={
                          handleLeaveChange
                        }
                        required
                      >

                        <option value="PENDING">
                          PENDING
                        </option>

                        <option value="APPROVED">
                          APPROVED
                        </option>

                        <option value="REJECTED">
                          REJECTED
                        </option>

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Reason
                      </label>

                      <input
                        type="text"
                        name="reason"
                        value={
                          leaveForm.reason
                        }
                        onChange={
                          handleLeaveChange
                        }
                        placeholder="Reason for leave"
                      />

                    </div>

                  </div>

                  <div className="form-actions">

                    <button type="submit">

                      {editingLeaveId
                        ? "Update Leave"
                        : "Add Leave"}

                    </button>

                    {editingLeaveId && (

                      <button
                        type="button"
                        onClick={
                          resetLeaveForm
                        }
                      >
                        Cancel Edit
                      </button>

                    )}

                  </div>

                </form>

              </section>

              <section className="data-section">

                <h2>
                  Leave Records
                </h2>

                {leaves.length === 0 ? (

                  <p>
                    No leave records found.
                  </p>

                ) : (

                  <div className="table-container">

                    <table>

                      <thead>

                        <tr>

                          <th>ID</th>
                          <th>Employee</th>
                          <th>Type</th>
                          <th>Start Date</th>
                          <th>End Date</th>
                          <th>Reason</th>
                          <th>Status</th>
                          <th>Actions</th>

                        </tr>

                      </thead>

                      <tbody>

                        {leaves.map(
                          (leave) => (

                            <tr
                              key={
                                leave.id
                              }
                            >

                              <td>
                                {
                                  leave.id
                                }
                              </td>

                              <td>
                                {
                                  leave.employee
                                    ?.employeeCode
                                }{" "}
                                -{" "}
                                {
                                  leave.employee
                                    ?.firstName
                                }{" "}
                                {
                                  leave.employee
                                    ?.lastName
                                }
                              </td>

                              <td>
                                {
                                  leave.leaveType
                                }
                              </td>

                              <td>
                                {
                                  leave.startDate
                                }
                              </td>

                              <td>
                                {
                                  leave.endDate
                                }
                              </td>

                              <td>
                                {
                                  leave.reason ||
                                  "-"
                                }
                              </td>

                              <td>

                                <span
                                  className={`leave-status ${String(
                                    leave.status || ""
                                  ).toLowerCase()}`}
                                >
                                  {
                                    leave.status
                                  }
                                </span>

                              </td>

                              <td>

                                <div className="table-actions">

                                  {leave.status ===
                                    "PENDING" && (

                                    <>
                                      <button
                                        type="button"
                                        onClick={() =>
                                          handleApproveLeave(
                                            leave
                                          )
                                        }
                                      >
                                        Approve
                                      </button>

                                      <button
                                        type="button"
                                        onClick={() =>
                                          handleRejectLeave(
                                            leave
                                          )
                                        }
                                      >
                                        Reject
                                      </button>
                                    </>

                                  )}

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleEditLeave(
                                        leave
                                      )
                                    }
                                  >
                                    Edit
                                  </button>

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleDeleteLeave(
                                        leave.id
                                      )
                                    }
                                  >
                                    Delete
                                  </button>

                                </div>

                              </td>

                            </tr>

                          )
                        )}

                      </tbody>

                    </table>

                  </div>

                )}

              </section>

            </>
          )}

          {/* =================================================
              PAYROLL
          ================================================= */}

          {adminSection ===
            "payroll" && (
            <>

              <section className="form-section">

                <h2>
                  {editingPayrollId
                    ? "Update Payroll"
                    : "Add Payroll"}
                </h2>

                <form
                  onSubmit={
                    handlePayrollSubmit
                  }
                  className="data-form"
                >

                  <div className="form-grid">

                    <div className="form-group">

                      <label>
                        Employee
                      </label>

                      <select
                        name="employeeId"
                        value={
                          payrollForm.employeeId
                        }
                        onChange={
                          handlePayrollChange
                        }
                        required
                      >

                        <option value="">
                          Select Employee
                        </option>

                        {employees.map(
                          (employee) => (

                            <option
                              key={
                                employee.id
                              }
                              value={
                                employee.id
                              }
                            >
                              {
                                employee.employeeCode
                              }{" "}
                              -{" "}
                              {
                                employee.firstName
                              }{" "}
                              {
                                employee.lastName
                              }
                            </option>

                          )
                        )}

                      </select>

                    </div>

                    <div className="form-group">

                      <label>
                        Payroll Month
                      </label>

                      <input
                        type="text"
                        name="month"
                        value={
                          payrollForm.month
                        }
                        onChange={
                          handlePayrollChange
                        }
                        placeholder="September 2026"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Basic Salary
                      </label>

                      <input
                        type="number"
                        name="basicSalary"
                        value={
                          payrollForm.basicSalary
                        }
                        onChange={
                          handlePayrollChange
                        }
                        placeholder="30000"
                        min="0"
                        step="0.01"
                        required
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Allowances
                      </label>

                      <input
                        type="number"
                        name="allowances"
                        value={
                          payrollForm.allowances
                        }
                        onChange={
                          handlePayrollChange
                        }
                        placeholder="5000"
                        min="0"
                        step="0.01"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Deductions
                      </label>

                      <input
                        type="number"
                        name="deductions"
                        value={
                          payrollForm.deductions
                        }
                        onChange={
                          handlePayrollChange
                        }
                        placeholder="2000"
                        min="0"
                        step="0.01"
                      />

                    </div>

                    <div className="form-group">

                      <label>
                        Payment Status
                      </label>

                      <select
                        name="paymentStatus"
                        value={
                          payrollForm.paymentStatus
                        }
                        onChange={
                          handlePayrollChange
                        }
                        required
                      >

                        <option value="PENDING">
                          PENDING
                        </option>

                        <option value="PAID">
                          PAID
                        </option>

                      </select>

                    </div>

                  </div>

                  <div className="form-actions">

                    <button type="submit">

                      {editingPayrollId
                        ? "Update Payroll"
                        : "Add Payroll"}

                    </button>

                    {editingPayrollId && (

                      <button
                        type="button"
                        onClick={
                          resetPayrollForm
                        }
                      >
                        Cancel Edit
                      </button>

                    )}

                  </div>

                  <p className="form-note">

                    Net Salary is calculated
                    automatically as:

                    <strong>
                      {" "}
                      Basic Salary +
                      Allowances -
                      Deductions
                    </strong>

                  </p>

                </form>

              </section>

              <section className="data-section">

                <h2>
                  Payroll Records
                </h2>

                {payrolls.length === 0 ? (

                  <p>
                    No payroll records found.
                  </p>

                ) : (

                  <div className="table-container">

                    <table>

                      <thead>

                        <tr>

                          <th>ID</th>
                          <th>Employee</th>
                          <th>Month</th>
                          <th>Basic Salary</th>
                          <th>Allowances</th>
                          <th>Deductions</th>
                          <th>Net Salary</th>
                          <th>Status</th>
                          <th>Actions</th>

                        </tr>

                      </thead>

                      <tbody>

                        {payrolls.map(
                          (payroll) => (

                            <tr
                              key={
                                payroll.id
                              }
                            >

                              <td>
                                {
                                  payroll.id
                                }
                              </td>

                              <td>
                                {
                                  payroll.employee
                                    ?.employeeCode
                                }{" "}
                                -{" "}
                                {
                                  payroll.employee
                                    ?.firstName
                                }{" "}
                                {
                                  payroll.employee
                                    ?.lastName
                                }
                              </td>

                              <td>
                                {
                                  payroll.month
                                }
                              </td>

                              <td>
                                ₹
                                {Number(
                                  payroll.basicSalary ||
                                    0
                                ).toLocaleString(
                                  "en-IN"
                                )}
                              </td>

                              <td>
                                ₹
                                {Number(
                                  payroll.allowances ||
                                    0
                                ).toLocaleString(
                                  "en-IN"
                                )}
                              </td>

                              <td>
                                ₹
                                {Number(
                                  payroll.deductions ||
                                    0
                                ).toLocaleString(
                                  "en-IN"
                                )}
                              </td>

                              <td>

                                <strong>
                                  ₹
                                  {Number(
                                    payroll.netSalary ||
                                      0
                                  ).toLocaleString(
                                    "en-IN"
                                  )}
                                </strong>

                              </td>

                              <td>
                                {
                                  payroll.paymentStatus
                                }
                              </td>

                              <td>

                                <div className="table-actions">

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleEditPayroll(
                                        payroll
                                      )
                                    }
                                  >
                                    Edit
                                  </button>

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleDeletePayroll(
                                        payroll.id
                                      )
                                    }
                                  >
                                    Delete
                                  </button>

                                </div>

                              </td>

                            </tr>

                          )
                        )}

                      </tbody>

                    </table>

                  </div>

                )}

              </section>

            </>
          )}

        </main>

      </div>
    );
  }

  /* =========================================================
     INVALID ROLE
  ========================================================= */

  return (
    <div className="loading-screen">

      <h2>
        Invalid user role.
      </h2>

      <button
        onClick={handleLogout}
      >
        Return to Login
      </button>

    </div>
  );
}

export default App;