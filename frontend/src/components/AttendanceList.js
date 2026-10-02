function AttendanceList({
  attendance = [],
  onEdit = () => {},
  onDelete = () => {},
}) {
  return (
    <div className="attendance-list">
      <h2>Attendance List</h2>

      {attendance.length === 0 ? (
        <p>No attendance records found.</p>
      ) : (
        <div className="attendance-table-container">
          <table className="attendance-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee Code</th>
                <th>Employee Name</th>
                <th>Department</th>
                <th>Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {attendance.map((record) => (
                <tr key={record.id}>
                  <td>{record.id}</td>

                  <td>
                    {record.employee?.employeeCode || ""}
                  </td>

                  <td>
                    {record.employee?.firstName || ""}{" "}
                    {record.employee?.lastName || ""}
                  </td>

                  <td>
                    {record.employee?.department?.departmentName || ""}
                  </td>

                  <td>{record.attendanceDate}</td>

                  <td>{record.status}</td>

                  <td>
                    <button
                      type="button"
                      onClick={() => onEdit(record)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      onClick={() => onDelete(record.id)}
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

export default AttendanceList;