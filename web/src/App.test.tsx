import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders Vankyle ID greeting', () => {
  render(<App />);
  const linkElement = screen.getByText(/It's all here with Vankyle ID./);
  expect(linkElement).toBeInTheDocument();
});
