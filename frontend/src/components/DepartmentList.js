function DepartmentList({
  departments = [],
  onEdit = () => {},
  onDelete = () => {},
}) {
  return (
    <div className="department-list">
      <h2>Department List</h2>

      {departments.length === 0 ? (
        <p>No departments found.</p>
      ) : (
        <div className="department-table-container">
          <table className="department-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Department Name</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {departments.map((department) => (
                <tr key={department.id}>
                  <td>{department.id}</td>
                  <td>{department.departmentName}</td>
                  <td>{department.description}</td>

                  <td>
                    <button
                      type="button"
                      onClick={() => onEdit(department)}
                    >
                      Edit
                    </button>

                    <button
                      type="button"
                      onClick={() => onDelete(department.id)}
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

export default DepartmentList;