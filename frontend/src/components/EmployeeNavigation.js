function EmployeeNavigation({
    activeSection,
    onNavigate,
}) {
    return (
        <nav className="employee-navigation">

            <button
                className={
                    activeSection === "profile"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("profile")
                }
            >
                My Profile
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
                My Attendance
            </button>

            <button
                className={
                    activeSection === "leave"
                        ? "active"
                        : ""
                }
                onClick={() =>
                    onNavigate("leave")
                }
            >
                My Leave
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
                My Payroll
            </button>

        </nav>
    );
}

export default EmployeeNavigation;