function AdminNavigation({
    activeSection,
    onNavigate,
}) {
    return (
        <nav className="admin-navigation">

            <button
                className={
                    activeSection === "dashboard"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("dashboard")
                }
            >
                Dashboard
            </button>

            <button
                className={
                    activeSection === "employees"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("employees")
                }
            >
                Employees
            </button>

            <button
                className={
                    activeSection === "departments"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("departments")
                }
            >
                Departments
            </button>

            <button
                className={
                    activeSection === "attendance"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("attendance")
                }
            >
                Attendance
            </button>

            <button
                className={
                    activeSection === "leaves"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("leaves")
                }
            >
                Leaves
            </button>

            <button
                className={
                    activeSection === "payroll"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("payroll")
                }
            >
                Payroll
            </button>

        </nav>
    );
}

export default AdminNavigation;