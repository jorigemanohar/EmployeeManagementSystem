import { useEffect, useState } from "react";
import { getMyProfile } from "../services/employeeProfileService";

function EmployeeProfile() {
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getMyProfile();

      setEmployee(response.data);
    } catch (error) {
      console.error(
        "Error loading employee profile:",
        error
      );

      if (
        error.response &&
        error.response.status === 403
      ) {
        setError(
          "You are not authorized to view your profile."
        );
      } else {
        setError(
          "Unable to load your profile."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <section className="employee-profile-section">
        <h2>My Profile</h2>
        <p>Loading profile...</p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="employee-profile-section">
        <h2>My Profile</h2>
        <div className="error-message">
          {error}
        </div>

        <button
          type="button"
          onClick={loadProfile}
        >
          Try Again
        </button>
      </section>
    );
  }

  if (!employee) {
    return (
      <section className="employee-profile-section">
        <h2>My Profile</h2>
        <p>Profile information not found.</p>
      </section>
    );
  }

  return (
    <section className="employee-profile-section">

      <div className="employee-profile-header">
        <div>
          <h2>My Profile</h2>
          <p>
            View your personal and employment
            information.
          </p>
        </div>

        <button
          type="button"
          onClick={loadProfile}
        >
          Refresh
        </button>
      </div>

      <div className="profile-card">

        <div className="profile-row">
          <span className="profile-label">
            Employee Code
          </span>

          <span className="profile-value">
            {employee.employeeCode || "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Name
          </span>

          <span className="profile-value">
            {employee.firstName || ""}{" "}
            {employee.lastName || ""}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Email
          </span>

          <span className="profile-value">
            {employee.email || "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Phone
          </span>

          <span className="profile-value">
            {employee.phone || "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Designation
          </span>

          <span className="profile-value">
            {employee.designation || "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Department
          </span>

          <span className="profile-value">
            {employee.department?.departmentName ||
              "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Joining Date
          </span>

          <span className="profile-value">
            {employee.joiningDate || "-"}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Salary
          </span>

          <span className="profile-value salary-value">
            ₹
            {Number(
              employee.salary || 0
            ).toLocaleString("en-IN")}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">
            Account Status
          </span>

          <span className="profile-value">

            {employee.accountCreated ? (
              <span className="account-status created">
                ✅ Created
              </span>
            ) : (
              <span className="account-status not-created">
                ❌ Not Created
              </span>
            )}

          </span>
        </div>

      </div>

    </section>
  );
}

export default EmployeeProfile;