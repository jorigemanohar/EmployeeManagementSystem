import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the Employee Management System login page', () => {
  render(<App />);

  expect(
    screen.getByRole('heading', {
      name: /employee management system/i,
    })
  ).toBeInTheDocument();

  expect(
    screen.getByRole('heading', {
      name: /^login$/i,
    })
  ).toBeInTheDocument();

  expect(
    screen.getByLabelText(/username/i)
  ).toBeInTheDocument();

  expect(
    screen.getByLabelText(/password/i)
  ).toBeInTheDocument();

  expect(
    screen.getByRole('button', {
      name: /^login$/i,
    })
  ).toBeInTheDocument();
});