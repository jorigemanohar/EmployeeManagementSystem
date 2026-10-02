function LeaveList({
  leaves = [],
  onEdit = () => {},
  onDelete = () => {},
}) {
  return (
    <div className="leave-list">
      <h2>Leave List</h2>

      {leaves.length === 0 ? (
        <p>No leave records found.</p>
      ) : (
        <div className="leave-table-container">
          <table className="leave-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee Code</th>
                <th>Employee Name</th>
                <th>Leave Type</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {leaves.map((leave) => (
                <tr key={leave.id}>
                  <td>{leave.id}</td>

                  <td>
                    {leave.employee?.employeeCode || ""}
                  </td>

                  <td>
                    {leave.employee?.firstName || ""}{" "}
                    {leave.employee?.lastName || ""}
                  </td>

                  <td>{leave.leaveType}</td>

                  <td>{leave.startDate}</td>

                  <td>{leave.endDate}</td>

                  <td>{leave.reason || ""}</td>

                  <td>{leave.status}</td>

                  <td>
                    <button
                      type="button"
                      onClick={() => onEdit(leave)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      onClick={() => onDelete(leave.id)}
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

export default LeaveList;