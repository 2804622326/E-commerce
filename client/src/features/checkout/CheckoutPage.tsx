import {
    Box,
    Button,
    Paper,
    Step,
    StepLabel,
    Stepper,
    Typography,
  } from "@mui/material";
  import { useState } from "react";
  import AddressForm from "./AddressForm";
  import PaymentForm from "./PaymentForm";
  import Review from "./Review";
  import { FormProvider, useForm } from "react-hook-form";
  import { yupResolver } from "@hookform/resolvers/yup";
  import agent from "../../app/api/agent";
  import { toast } from "react-toastify";
  
      import { BasketItem } from "../../app/models/basket";
  import { useAppDispatch } from "../../app/store/configureStore";
  import { setBasket } from "../basket/basketSlice";
import { ValidationRules } from "./validationRules";
  
  
  const steps = ["Shipping address", "Review your order", "Payment details"];
  
  function getStepContent(step: number) {
    switch (step) {
      case 0:
        return <AddressForm />;
      case 1:
        return <Review />;
      case 2:
        return <PaymentForm />;
      default:
        throw new Error("Unknown step");
    }
  }
  
  export default function CheckoutPage() {
    const [activeStep, setActiveStep] = useState(0);
    const [orderNumber, setOrderNumber] = useState(0);
    const [loading, setLoading] = useState(false);
    const currentValdationRule = ValidationRules[activeStep];
    const methods = useForm({
      mode: "all",
      resolver: yupResolver(currentValdationRule),
    }); // Initialize useForm
    const dispatch = useAppDispatch();
    const handleNext = async () => {
      // Trigger form validation
      const isValid = await methods.trigger();
  
      if (isValid) {
        // Log form data before moving to the next step
        //console.log(methods.getValues());
        const data: any = methods.getValues();
        console.log(data);
        if (activeStep === steps.length - 1) {
          // If it's the last step, submit the order
          const basket = await agent.Basket.get();
          if (basket) {
            const subTotal = calculateSubTotal(basket.items);
  
            // Add logic to calculate delivery fee
            const deliveryFee = 200;
  
            try {
              setLoading(true);
              // Construct the order DTO to send to the backend
              const orderDto = {
                basketId: basket.id,
                shippingAddress: {
                  name: data.firstName + " " + data.lastName,
                  address1: data.address1,
                  address2: data.address2,
                  city: data.city,
                  state: data.state,
                  zipCode: data.zip,
                  country: data.country,
                },
                subTotal: subTotal,
                deliveryFee: deliveryFee              
              };
  
              // Call the API to create the order
              const orderId = await agent.Orders.create(orderDto);
              // Order created successfully
              setOrderNumber(orderId); 
              setActiveStep(activeStep + 1); // Move to the next step           
              
              // Clear the basket from local storage and Redux state
              localStorage.removeItem('basket_id');
              localStorage.removeItem('basket');
              dispatch(setBasket(null));
              
              // Try to delete the basket from API
              // Note: This might fail with 404 if the basket was already deleted by the backend
              // after order creation, which is acceptable
              if (basket.id) {
                try {
                  await agent.Basket.deleteBasket(basket.id);
                  console.log("Basket deleted from server successfully");
                } catch (deleteError: any) {
                  // Silently handle deletion errors - basket might already be deleted
                  if (deleteError?.response?.status === 404) {
                    console.log("Basket already removed from server");
                  } else {
                    console.log("Note: Could not delete basket from server, but order was created successfully");
                  }
                }
              }                 
            } catch (error) {
              // Handle API call errors
              console.error("Error submitting the order:", error);
              toast.error("Failed to submit the order. Please try again.");
            } finally {
              setLoading(false);
            }
          } else {
            console.error("Basket not found in local storage.");
          }
        } else {
          // Move to the next step if it's not the last step
          setActiveStep(activeStep + 1);
        }
      }
    };
  
    const handleBack = () => {
      setActiveStep(activeStep - 1);
    };
  
    const calculateSubTotal = (items: BasketItem[]): number => {
      return items.reduce((total, item) => total + item.price * item.quantity, 0);
    };
  
    return (
      <FormProvider {...methods}>
        <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
          <Typography component="h1" variant="h4" align="center">
            Checkout
          </Typography>
          <Stepper activeStep={activeStep} sx={{ pt: 3, pb: 5 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
          <>
            {activeStep === steps.length ? (
              <>
                <Typography variant="h5" gutterBottom>
                  Thank you for your order.
                </Typography>
                <Typography variant="subtitle1">
                  Your order number is #{orderNumber}. We have emailed your order confirmation, and will send you an update when your order has shipped.
                </Typography>
              </>
            ) : (
              <>
                {getStepContent(activeStep)}
                <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
                  {activeStep !== 0 && (
                    <Button onClick={handleBack} sx={{ mt: 3, ml: 1 }}>
                      Back
                    </Button>
                  )}
                  <Button
                    variant="contained"
                    onClick={handleNext}
                    sx={{ mt: 3, ml: 1 }}
                    disabled={loading}
                  >
                    {activeStep === steps.length - 1 ? "Place order" : "Next"}
                  </Button>
                </Box>
              </>
            )}
          </>
        </Paper>
      </FormProvider>
    );
  }
  