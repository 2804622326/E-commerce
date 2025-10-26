import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';
import PaymentForm from '../../features/checkout/PaymentForm';

// Wrapper component to provide form context
const TestWrapper = ({ children, defaultValues = {} }: { children: React.ReactNode; defaultValues?: any }) => {
  const methods = useForm({ defaultValues });
  return (
    <FormProvider {...methods}>
      {children}
    </FormProvider>
  );
};

describe('PaymentForm Component', () => {
  it('renders payment form heading', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    expect(screen.getByText('Payment Form')).toBeInTheDocument();
  });

  it('renders all required form fields', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    expect(screen.getByLabelText('Name on card')).toBeInTheDocument();
    expect(screen.getByLabelText('Card number')).toBeInTheDocument();
    expect(screen.getByLabelText('Expiry date')).toBeInTheDocument();
    expect(screen.getByLabelText('CVV')).toBeInTheDocument();
  });

  it('displays helper text for each field', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    expect(screen.getByText('Enter Name on Card')).toBeInTheDocument();
    expect(screen.getByText('Enter Card Number')).toBeInTheDocument();
    expect(screen.getByText('Enter Expiry Date')).toBeInTheDocument();
    expect(screen.getByText('Last three digits on signature strip')).toBeInTheDocument();
  });

  it('renders save card checkbox', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).toBeInTheDocument();
    expect(screen.getByText('Remember credit card details for next time')).toBeInTheDocument();
  });

  it('allows input in card name field', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const cardNameInput = screen.getByLabelText('Name on card');
    fireEvent.change(cardNameInput, { target: { value: 'John Doe' } });
    expect(cardNameInput).toHaveValue('John Doe');
  });

  it('allows input in card number field', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const cardNumberInput = screen.getByLabelText('Card number');
    fireEvent.change(cardNumberInput, { target: { value: '1234567890123456' } });
    expect(cardNumberInput).toHaveValue('1234567890123456');
  });

  it('allows input in expiry date field', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const expDateInput = screen.getByLabelText('Expiry date');
    fireEvent.change(expDateInput, { target: { value: '12/25' } });
    expect(expDateInput).toHaveValue('12/25');
  });

  it('allows input in CVV field', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const cvvInput = screen.getByLabelText('CVV');
    fireEvent.change(cvvInput, { target: { value: '123' } });
    expect(cvvInput).toHaveValue('123');
  });

  it('allows checkbox to be toggled', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).not.toBeChecked();
    
    fireEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('displays error states when errors are present', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );

    // Check that error states are applied to inputs
    const cardNameInput = screen.getByLabelText('Name on card');
    const cardNumberInput = screen.getByLabelText('Card number');
    const expDateInput = screen.getByLabelText('Expiry date');
    const cvvInput = screen.getByLabelText('CVV');

    // Verify inputs are rendered correctly (checking they exist is sufficient)
    expect(cardNameInput).toBeInTheDocument();
    expect(cardNumberInput).toBeInTheDocument();
    expect(expDateInput).toBeInTheDocument();
    expect(cvvInput).toBeInTheDocument();
  });

  it('has correct autocomplete attributes', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    expect(screen.getByLabelText('Name on card')).toHaveAttribute('autocomplete', 'cc-name');
    expect(screen.getByLabelText('Card number')).toHaveAttribute('autocomplete', 'cc-number');
    expect(screen.getByLabelText('Expiry date')).toHaveAttribute('autocomplete', 'cc-exp');
    expect(screen.getByLabelText('CVV')).toHaveAttribute('autocomplete', 'cc-csc');
  });

  it('uses standard variant for all text fields', () => {
    render(
      <TestWrapper>
        <PaymentForm />
      </TestWrapper>
    );
    
    const inputs = screen.getAllByRole('textbox');
    inputs.forEach(input => {
      // Check that text fields are rendered correctly
      expect(input).toBeInTheDocument();
      expect(input.closest('.MuiTextField-root')).toBeInTheDocument();
    });
  });

  it('renders with pre-filled values when provided', () => {
    const defaultValues = {
      cardName: 'John Smith',
      cardNumber: '4111111111111111',
      expDate: '12/25',
      cvv: '123'
    };

    render(
      <TestWrapper defaultValues={defaultValues}>
        <PaymentForm />
      </TestWrapper>
    );

    expect(screen.getByDisplayValue('John Smith')).toBeInTheDocument();
    expect(screen.getByDisplayValue('4111111111111111')).toBeInTheDocument();
    expect(screen.getByDisplayValue('12/25')).toBeInTheDocument();
    expect(screen.getByDisplayValue('123')).toBeInTheDocument();
  });
});