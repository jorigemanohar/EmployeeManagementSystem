import { useState } from "react";
import { createEmployeeAccount } from "../services/employeeService";

function EmployeeAccountForm({
  employees,
  onAccountCreated,
}) {
  const [employeeId, setEmployeeId] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  /*
   * Only employees without an account
   * should appear in the dropdown.
   */
  const employeesWithoutAccount =
    employees.filter(
      (employee) =>
        employee.accountCreated !== true
    );

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    if (!employeeId) {
      setError("Please select an employee.");
      return;
    }

    if (!username.trim()) {
      setError("Please enter a username.");
      return;
    }

    if (!password) {
      setError("Please enter a password.");
      return;
    }

    if (password.length < 6) {
      setError(
        "Password must contain at least 6 characters."
      );
      return;
    }

    setLoading(true);

    try {
      const response =
        await createEmployeeAccount(
          Number(employeeId),
          {
            username: username.trim(),
            password: password,
          }
        );

      const createdEmployee =
        response.data?.employee;

      setMessage(
        `Login account created successfully for ${
          createdEmployee
            ? `${createdEmployee.employeeCode} - ${createdEmployee.firstName} ${createdEmployee.lastName}`
            : "employee"
        }.`
      );

      /*
       * Clear the form after successful creation.
       */
      setEmployeeId("");
      setUsername("");
      setPassword("");

      /*
       * Refresh employees so the newly created
       * account immediately changes to Created.
       */
      if (onAccountCreated) {
        await onAccountCreated();
      }
    } catch (error) {
      console.error(
        "Error creating employee account:",
        error
      );

      if (
        error.response &&
        error.response.status === 403
      ) {
        setError(
          "Only an administrator can create employee accounts."
        );
      } else if (
        error.response &&
        error.response.status === 404
      ) {
        setError(
          "Employee not found."
        );
      } else if (
        error.response &&
        error.response.status === 400
      ) {
        if (
          typeof error.response.data ===
          "string"
        ) {
          setError(
            error.response.data
          );
        } else {
          setError(
            "Unable to create account. Check the username or employee account status."
          );
        }
      } else if (
        error.response &&
        typeof error.response.data ===
          "string"
      ) {
        setError(
          error.response.data
        );
      } else {
        setError(
          "Unable to create employee account."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="employee-account-section">

      <h2>
        Create Employee Login Account
      </h2>

      <p>
        Create login credentials for an
        employee who does not already have
        an account.
      </p>

      {/* =================================================
          NO EMPLOYEES WITHOUT ACCOUNT
      ================================================= */}

      {employeesWithoutAccount.length ===
      0 ? (
        <div className="success-message">
          All employees already have login
          accounts.
        </div>
      ) : (
        <form
          onSubmit={handleSubmit}
          className="data-form"
        >

          {/* =================================================
              EMPLOYEE
          ================================================= */}

          <div className="form-group">

            <label htmlFor="accountEmployee">
              Employee
            </label>

            <select
              id="accountEmployee"
              value={employeeId}
              onChange={(e) =>
                setEmployeeId(
                  e.target.value
                )
              }
              disabled={loading}
              required
            >

              <option value="">
                Select Employee
              </option>

              {employeesWithoutAccount.map(
                (employee) => (
                  <option
                    key={employee.id}
                    value={employee.id}
                  >
                    {employee.employeeCode} -{" "}
                    {employee.firstName}{" "}
                    {employee.lastName}
                  </option>
                )
              )}

            </select>

          </div>

          {/* =================================================
              USERNAME
          ================================================= */}

          <div className="form-group">

            <label htmlFor="accountUsername">
              Username
            </label>

            <input
              id="accountUsername"
              type="text"
              value={username}
              onChange={(e) =>
                setUsername(
                  e.target.value
                )
              }
              placeholder="Enter login username"
              autoComplete="off"
              disabled={loading}
              required
            />

          </div>

          {/* =================================================
              PASSWORD
          ================================================= */}

          <div className="form-group">

            <label htmlFor="accountPassword">
              Password
            </label>

            <input
              id="accountPassword"
              type="password"
              value={password}
              onChange={(e) =>
                setPassword(
                  e.target.value
                )
              }
              placeholder="Enter login password"
              autoComplete="new-password"
              disabled={loading}
              required
            />

          </div>

          {/* =================================================
              ERROR
          ================================================= */}

          {error && (
            <p className="error-message">
              {error}
            </p>
          )}

          {/* =================================================
              SUCCESS
          ================================================= */}

          {message && (
            <p className="success-message">
              {message}
            </p>
          )}

          {/* =================================================
              SUBMIT
          ================================================= */}

          <button
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Creating Account..."
              : "Create Employee Account"}
          </button>

        </form>
      )}

    </section>
  );
}

export default EmployeeAccountForm;