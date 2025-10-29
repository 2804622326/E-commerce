import { Product } from '../../app/models/product';
import { Basket, BasketItem } from '../../app/models/basket';

export const mockProduct: Product = {
  id: 1,
  name: 'Test Product',
  description: 'This is a test product description',
  price: 1000,
  pictureUrl: '/images/products/test-product.jpg',
  productType: 'Test Type',
  productBrand: 'Test Brand',
};

export const mockProducts: Product[] = [
  {
    id: 1,
    name: 'Tennis Racket',
    description: 'Professional tennis racket',
    price: 2500,
    pictureUrl: '/images/products/racket.jpg',
    productType: 'Equipment',
    productBrand: 'Wilson',
  },
  {
    id: 2,
    name: 'Football',
    description: 'Official size football',
    price: 800,
    pictureUrl: '/images/products/football.jpg',
    productType: 'Ball',
    productBrand: 'Nike',
  },
  {
    id: 3,
    name: 'Basketball Shoes',
    description: 'High performance basketball shoes',
    price: 4500,
    pictureUrl: '/images/products/shoes.jpg',
    productType: 'Footwear',
    productBrand: 'Adidas',
  },
];

export const mockBasketItem: BasketItem = {
  id: 1,
  name: 'Tennis Racket',
  description: 'Professional tennis racket',
  price: 2500,
  pictureUrl: '/images/products/racket.jpg',
  productBrand: 'Wilson',
  productType: 'Equipment',
  quantity: 2,
};

export const mockBasket: Basket = {
  id: 'basket-123',
  items: [
    {
      id: 1,
      name: 'Tennis Racket',
      description: 'Professional tennis racket',
      price: 2500,
      pictureUrl: '/images/products/racket.jpg',
      productBrand: 'Wilson',
      productType: 'Equipment',
      quantity: 2,
    },
    {
      id: 2,
      name: 'Football',
      description: 'Official size football',
      price: 800,
      pictureUrl: '/images/products/football.jpg',
      productBrand: 'Nike',
      productType: 'Ball',
      quantity: 1,
    },
  ],
};

export const mockUser = {
  email: 'test@example.com',
  token: 'mock-jwt-token',
};
