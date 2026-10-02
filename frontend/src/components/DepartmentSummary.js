function DepartmentSummary({
    departments,
    employees,
}) {
    return (
        <div className="department-summary">

            <h3>
                Department Overview
            </h3>

            {departments.length === 0 ? (
                <p>
                    No departments found.
                </p>
            ) : (
                <div className="department-summary-list">

                    {departments.map(
                        (department) => {

                            const employeeCount =
                                employees.filter(
                                    (employee) =>
                                        employee.department?.id ===
                                        department.id
                                ).length;

                            return (
                                <div
                                    className="department-summary-item"
                                    key={department.id}
                                >

                                    <div>
                                        <strong>
                                            {
                                                department.departmentName
                                            }
                                        </strong>

                                        <span>
                                            {
                                                department.description ||
                                                "No description"
                                            }
                                        </span>
                                    </div>

                                    <div className="department-employee-count">
                                        {employeeCount}

                                        <span>
                                            {employeeCount === 1
                                                ? " employee"
                                                : " employees"}
                                        </span>
                                    </div>

                                </div>
                            );
                        }
                    )}

                </div>
            )}

        </div>
    );
}

export default DepartmentSummary;