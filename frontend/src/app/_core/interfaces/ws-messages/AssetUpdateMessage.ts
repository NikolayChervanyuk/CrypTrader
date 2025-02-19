export interface AssetUpdateMessage {
  type: string;
  asset: AssetModel;
}

export interface AssetModel {
  symbol: string;
  price: number;
}
