import { useEffect, useState } from "react";

function PayrollForm({
  employees = [],
  onSubmit,
  editingPayroll = null,
  onCancel = () => {},
}) {
  const [employeeId, setEmployeeId] = useState("");
  const [month, setMonth] = useState("");
  const [basicSalary, setBasicSalary] = useState("");
  const [allowances, setAllowances] = useState("");
  const [deductions, setDeductions] = useState("");
  const [netSalary, setNetSalary] = useState("");
  const [paymentStatus, setPaymentStatus] = useState("PENDING");

  useEffect(() => {
    if (editingPayroll) {
      setEmployeeId(editingPayroll.employee?.id || "");
      setMonth(editingPayroll.month || "");
      setBasicSalary(editingPayroll.basicSalary ?? "");
      setAllowances(editingPayroll.allowances ?? "");
      setDeductions(editingPayroll.deductions ?? "");
      setNetSalary(editingPayroll.netSalary ?? "");
      setPaymentStatus(editingPayroll.paymentStatus || "PENDING");
    } else {
      setEmployeeId("");
      setMonth("");
      setBasicSalary("");
      setAllowances("");
      setDeductions("");
      setNetSalary("");
      setPaymentStatus("PENDING");
    }
  }, [editingPayroll]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const payroll = {
      employee: {
        id: Number(employeeId),
      },
      month,
      basicSalary: Number(basicSalary),
      allowances: Number(allowances),
      deductions: Number(deductions),
      netSalary: Number(netSalary),
      paymentStatus,
    };

    onSubmit(payroll);
  };

  return (
    <div className="payroll-form">
      <h2>{editingPayroll ? "Edit Payroll" : "Add Payroll"}</h2>

      <form onSubmit={handleSubmit}>

        <div>
          <label>Employee</label>
          <select
            value={employeeId}
            onChange={(e) => setEmployeeId(e.target.value)}
            required
          >
            <option value="">Select Employee</option>

            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.employeeCode} - {employee.firstName}{" "}
                {employee.lastName}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label>Month</label>
          <input
            type="text"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            placeholder="September 2026"
            required
          />
        </div>

        <div>
          <label>Basic Salary</label>
          <input
            type="number"
            value={basicSalary}
            onChange={(e) => setBasicSalary(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Allowances</label>
          <input
            type="number"
            value={allowances}
            onChange={(e) => setAllowances(e.target.value)}
          />
        </div>

        <div>
          <label>Deductions</label>
          <input
            type="number"
            value={deductions}
            onChange={(e) => setDeductions(e.target.value)}
          />
        </div>

        <div>
          <label>Net Salary</label>
          <input
            type="number"
            value={netSalary}
            onChange={(e) => setNetSalary(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Payment Status</label>
          <select
            value={paymentStatus}
            onChange={(e) => setPaymentStatus(e.target.value)}
            required
          >
            <option value="PENDING">Pending</option>
            <option value="PAID">Paid</option>
          </select>
        </div>

        <button type="submit">
          {editingPayroll ? "Update Payroll" : "Add Payroll"}
        </button>

        {editingPayroll && (
          <button type="button" onClick={onCancel}>
            Cancel
          </button>
        )}

      </form>
    </div>
  );
}

export default PayrollForm;