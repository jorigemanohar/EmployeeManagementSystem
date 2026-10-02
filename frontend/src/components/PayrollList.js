function PayrollList({
  payrolls = [],
  onEdit = () => {},
  onDelete = () => {},
}) {
  return (
    <div className="payroll-list">
      <h2>Payroll List</h2>

      {payrolls.length === 0 ? (
        <p>No payroll records found.</p>
      ) : (
        <div className="payroll-table-container">
          <table className="payroll-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee Code</th>
                <th>Employee Name</th>
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
              {payrolls.map((payroll) => (
                <tr key={payroll.id}>
                  <td>{payroll.id}</td>

                  <td>
                    {payroll.employee?.employeeCode || ""}
                  </td>

                  <td>
                    {payroll.employee?.firstName || ""}{" "}
                    {payroll.employee?.lastName || ""}
                  </td>

                  <td>{payroll.month}</td>

                  <td>₹{payroll.basicSalary}</td>

                  <td>₹{payroll.allowances || 0}</td>

                  <td>₹{payroll.deductions || 0}</td>

                  <td>₹{payroll.netSalary}</td>

                  <td>{payroll.paymentStatus}</td>

                  <td>
                    <button
                      type="button"
                      onClick={() => onEdit(payroll)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      onClick={() => onDelete(payroll.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default PayrollList;