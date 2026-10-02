import { useState } from "react";
import axios from "axios";

function Login({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    const trimmedUsername =
      username.trim();

    if (!trimmedUsername) {
      setError("Please enter your username.");
      return;
    }

    if (!password) {
      setError("Please enter your password.");
      return;
    }

    setLoading(true);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        {
          username: trimmedUsername,
          password: password,
        }
      );

      const token = response.data;

      if (!token || typeof token !== "string") {
        setError(
          "Invalid login response from server."
        );
        return;
      }

      /*
       * Store the JWT.
       * App.js also receives the token through onLogin()
       * and determines the user's role.
       */
      localStorage.setItem(
        "token",
        token
      );

      if (onLogin) {
        onLogin(token);
      }
    } catch (err) {
      console.error(
        "Login error:",
        err
      );

      if (err.response) {
        if (
          err.response.status === 401
        ) {
          setError(
            "Invalid username or password."
          );
        } else if (
          typeof err.response.data ===
          "string"
        ) {
          setError(
            err.response.data
          );
        } else {
          setError(
            "Login failed. Please try again."
          );
        }
      } else {
        setError(
          "Unable to connect to the server. Make sure Spring Boot is running."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">

      <div className="login-card">

        <h1>
          Employee Management System
        </h1>

        <h2>
          Login
        </h2>

        <form
          onSubmit={handleSubmit}
        >

          <div className="form-group">

            <label htmlFor="username">
              Username
            </label>

            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) =>
                setUsername(
                  e.target.value
                )
              }
              placeholder="Enter username"
              autoComplete="username"
              disabled={loading}
              required
            />

          </div>

          <div className="form-group">

            <label htmlFor="password">
              Password
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) =>
                setPassword(
                  e.target.value
                )
              }
              placeholder="Enter password"
              autoComplete="current-password"
              disabled={loading}
              required
            />

          </div>

          {error && (
            <p className="error-message">
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Logging in..."
              : "Login"}
          </button>

        </form>

      </div>

    </div>
  );
}

export default Login;