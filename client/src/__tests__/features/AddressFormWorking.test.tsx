import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useForm, FormProvider } from 'react-hook-form';
import AddressForm from '../../features/checkout/AddressForm';

function AddressFormWrapper() {
  const methods = useForm({
    defaultValues: {
      firstName: '',
      lastName: '',
      address1: '',
      address2: '',
      city: '',
      state: '',
      zip: '',
      country: '',
    },
  });

  return (
    <FormProvider {...methods}>
      <AddressForm />
    </FormProvider>
  );
}

describe('AddressForm - Form Rendering', () => {
  it('renders shipping address heading', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByText(/shipping address/i)).toBeInTheDocument();
  });

  it('renders first name field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
  });

  it('renders last name field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
  });

  it('renders address line 1 field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/address line 1/i)).toBeInTheDocument();
  });

  it('renders address line 2 field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/address line 2/i)).toBeInTheDocument();
  });

  it('renders city field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/city/i)).toBeInTheDocument();
  });

  it('renders state field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/state\/province\/region/i)).toBeInTheDocument();
  });

  it('renders zip code field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/zip.*postal code/i)).toBeInTheDocument();
  });

  it('renders country field', () => {
    render(<AddressFormWrapper />);
    expect(screen.getByLabelText(/country/i)).toBeInTheDocument();
  });
});

describe('AddressForm - User Interactions', () => {
  it('allows typing in first name field', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const firstNameInput = screen.getByLabelText(/first name/i);
    await user.type(firstNameInput, 'John');
    
    expect(firstNameInput).toHaveValue('John');
  });

  it('allows typing in last name field', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const lastNameInput = screen.getByLabelText(/last name/i);
    await user.type(lastNameInput, 'Doe');
    
    expect(lastNameInput).toHaveValue('Doe');
  });

  it('allows typing in address line 1', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const addressInput = screen.getByLabelText(/address line 1/i);
    await user.type(addressInput, '123 Main Street');
    
    expect(addressInput).toHaveValue('123 Main Street');
  });

  it('allows typing in city field', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const cityInput = screen.getByLabelText(/city/i);
    await user.type(cityInput, 'Mumbai');
    
    expect(cityInput).toHaveValue('Mumbai');
  });

  it('allows typing in zip code field', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const zipInput = screen.getByLabelText(/zip.*postal code/i);
    await user.type(zipInput, '400001');
    
    expect(zipInput).toHaveValue('400001');
  });

  it('allows typing in country field', async () => {
    const user = userEvent.setup();
    render(<AddressFormWrapper />);
    
    const countryInput = screen.getByLabelText(/country/i);
    await user.type(countryInput, 'India');
    
    expect(countryInput).toHaveValue('India');
  });

  it('marks required fields', () => {
    render(<AddressFormWrapper />);
    
    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const addressInput = screen.getByLabelText(/address line 1/i);
    const cityInput = screen.getByLabelText(/city/i);
    const zipInput = screen.getByLabelText(/zip.*postal code/i);
    const countryInput = screen.getByLabelText(/country/i);
    
    expect(firstNameInput).toBeRequired();
    expect(lastNameInput).toBeRequired();
    expect(addressInput).toBeRequired();
    expect(cityInput).toBeRequired();
    expect(zipInput).toBeRequired();
    expect(countryInput).toBeRequired();
  });
});
