function EmployeeList({
  employees = [],
  onEdit = () => {},
  onDelete = () => {},
}) {
  return (
    <div className="employee-list">
      <h2>Employee List</h2>

      {employees.length === 0 ? (
        <p>No employees found.</p>
      ) : (
        <div className="employee-table-container">
          <table className="employee-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee Code</th>
                <th>Name</th>
                <th>Email</th>
                <th>Department</th>
                <th>Designation</th>
                <th>Salary</th>
                <th>Joining Date</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {employees.map((employee) => (
                <tr key={employee.id}>
                  <td>{employee.id}</td>

                  <td>{employee.employeeCode}</td>

                  <td>
                    {employee.firstName} {employee.lastName}
                  </td>

                  <td>{employee.email}</td>

                  <td>
                    {employee.department?.departmentName || ""}
                  </td>

                  <td>{employee.designation}</td>

                  <td>₹{employee.salary}</td>

                  <td>{employee.joiningDate}</td>

                  <td>
                    <button
                      type="button"
                      onClick={() => onEdit(employee)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      onClick={() => onDelete(employee.id)}
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

export default EmployeeList;