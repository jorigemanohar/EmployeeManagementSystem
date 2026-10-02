import { useEffect, useState } from "react";

function LeaveForm({
  employees = [],
  onSubmit,
  editingLeave = null,
  onCancel = () => {},
}) {
  const [employeeId, setEmployeeId] = useState("");
  const [leaveType, setLeaveType] = useState("CASUAL");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [reason, setReason] = useState("");
  const [status, setStatus] = useState("PENDING");

  useEffect(() => {
    if (editingLeave) {
      setEmployeeId(editingLeave.employee?.id || "");
      setLeaveType(editingLeave.leaveType || "CASUAL");
      setStartDate(editingLeave.startDate || "");
      setEndDate(editingLeave.endDate || "");
      setReason(editingLeave.reason || "");
      setStatus(editingLeave.status || "PENDING");
    } else {
      setEmployeeId("");
      setLeaveType("CASUAL");
      setStartDate("");
      setEndDate("");
      setReason("");
      setStatus("PENDING");
    }
  }, [editingLeave]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const leave = {
      employee: {
        id: Number(employeeId),
      },
      leaveType,
      startDate,
      endDate,
      reason,
      status,
    };

    onSubmit(leave);
  };

  return (
    <div className="leave-form">
      <h2>{editingLeave ? "Edit Leave" : "Add Leave"}</h2>

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
          <label>Leave Type</label>

          <select
            value={leaveType}
            onChange={(e) => setLeaveType(e.target.value)}
            required
          >
            <option value="CASUAL">Casual</option>
            <option value="SICK">Sick</option>
            <option value="ANNUAL">Annual</option>
            <option value="OTHER">Other</option>
          </select>
        </div>

        <div>
          <label>Start Date</label>

          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </div>

        <div>
          <label>End Date</label>

          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Reason</label>

          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Enter reason for leave"
          />
        </div>

        <div>
          <label>Status</label>

          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            required
          >
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>

        <button type="submit">
          {editingLeave ? "Update Leave" : "Add Leave"}
        </button>

        {editingLeave && (
          <button type="button" onClick={onCancel}>
            Cancel
          </button>
        )}
      </form>
    </div>
  );
}

export default LeaveForm;