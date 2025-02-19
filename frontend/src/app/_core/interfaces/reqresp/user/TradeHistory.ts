export interface TradeHistory {
  tradeType: 'BUY' | 'SELL';
  symbol: string;
  quantity: number;
  price: number;
  date: number; // Api returns timestamp in seconds
}
