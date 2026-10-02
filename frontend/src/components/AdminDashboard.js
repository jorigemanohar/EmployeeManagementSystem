function AdminDashboard({
    employees,
    departments,
    attendance,
    leaves,
    payrolls,
}) {
    const today = new Date()
        .toISOString()
        .split("T")[0];

    const presentToday = attendance.filter(
        (record) =>
            record.attendanceDate === today &&
            record.status === "PRESENT"
    ).length;

    const absentToday = attendance.filter(
        (record) =>
            record.attendanceDate === today &&
            record.status === "ABSENT"
    ).length;

    const pendingLeaves = leaves.filter(
        (leave) => leave.status === "PENDING"
    ).length;

    const approvedLeaves = leaves.filter(
        (leave) => leave.status === "APPROVED"
    ).length;

    const rejectedLeaves = leaves.filter(
        (leave) => leave.status === "REJECTED"
    ).length;

    const paidPayrolls = payrolls.filter(
        (payroll) => payroll.paymentStatus === "PAID"
    ).length;

    const pendingPayrolls = payrolls.filter(
        (payroll) => payroll.paymentStatus === "PENDING"
    ).length;

    const totalPayroll = payrolls.reduce(
        (total, payroll) =>
            total + Number(payroll.netSalary || 0),
        0
    );

    return (
        <section className="admin-dashboard-summary">

            <h2>Dashboard Overview</h2>

            {/* =========================
                MAIN DASHBOARD CARDS
                ========================= */}

            <div className="dashboard-cards">

                <div className="dashboard-card">
                    <h3>Total Employees</h3>
                    <p>
                        {employees.length}
                    </p>
                </div>

                <div className="dashboard-card">
                    <h3>Total Departments</h3>
                    <p>
                        {departments.length}
                    </p>
                </div>

                <div className="dashboard-card">
                    <h3>Present Today</h3>
                    <p>
                        {presentToday}
                    </p>
                </div>

                <div className="dashboard-card">
                    <h3>Absent Today</h3>
                    <p>
                        {absentToday}
                    </p>
                </div>

                <div className="dashboard-card">
                    <h3>Pending Leaves</h3>
                    <p>
                        {pendingLeaves}
                    </p>
                </div>

                <div className="dashboard-card">
                    <h3>Total Payroll</h3>
                    <p>
                        ₹
                        {totalPayroll.toLocaleString(
                            "en-IN"
                        )}
                    </p>
                </div>

            </div>

            {/* =========================
                PAYROLL SUMMARY
                ========================= */}

            <div className="dashboard-summary-grid">

                <div className="dashboard-summary-box">

                    <h3>
                        Payroll Summary
                    </h3>

                    <div className="summary-row">
                        <span>
                            Paid Payroll
                        </span>

                        <strong>
                            {paidPayrolls}
                        </strong>
                    </div>

                    <div className="summary-row">
                        <span>
                            Pending Payroll
                        </span>

                        <strong>
                            {pendingPayrolls}
                        </strong>
                    </div>

                    <div className="summary-row">
                        <span>
                            Total Payroll Records
                        </span>

                        <strong>
                            {payrolls.length}
                        </strong>
                    </div>

                </div>


                {/* =========================
                    LEAVE SUMMARY
                    ========================= */}

                <div className="dashboard-summary-box">

                    <h3>
                        Leave Summary
                    </h3>

                    <div className="summary-row">
                        <span>
                            Pending
                        </span>

                        <strong>
                            {pendingLeaves}
                        </strong>
                    </div>

                    <div className="summary-row">
                        <span>
                            Approved
                        </span>

                        <strong>
                            {approvedLeaves}
                        </strong>
                    </div>

                    <div className="summary-row">
                        <span>
                            Rejected
                        </span>

                        <strong>
                            {rejectedLeaves}
                        </strong>
                    </div>

                </div>

            </div>

        </section>
    );
}

export default AdminDashboard;