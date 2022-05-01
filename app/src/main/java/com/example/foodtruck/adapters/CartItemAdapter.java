package com.example.foodtruck.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodtruck.OrderSummeryActivity;
import com.example.foodtruck.ProductDetailsActivity;
import com.example.foodtruck.R;
import com.example.foodtruck.firebase.FirebaseQueries;
import com.example.foodtruck.models.CartItemModel;
import com.example.foodtruck.ui.myCart.MyCartFragment;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter {
    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView totalAmountInCartFragment;
    private Boolean removeLayout;

    public CartItemAdapter(List<CartItemModel> cartItemModelList, TextView totalAmountInCartFragment, boolean removeLayout) {
        this.cartItemModelList = cartItemModelList;
        this.totalAmountInCartFragment = totalAmountInCartFragment;
        this.removeLayout = removeLayout;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM_VIEW;
            case 1:
                return CartItemModel.BALANCE_DETAILS_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM_VIEW:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new cartItemViewHolder(cartItemView);
            case CartItemModel.BALANCE_DETAILS_VIEW:
                View balanceDetailsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_balance_details_layout, parent, false);
                return new balanceDetailsViewHolder(balanceDetailsView);
            default:
                return null;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM_VIEW:
                String productId = cartItemModelList.get(position).getProductID();
                String resources = cartItemModelList.get(position).getCartProductImage();
                String name = cartItemModelList.get(position).getCartProductName();
                Long freeCoupon = cartItemModelList.get(position).getFreeCouponNumber();
                String originalPrice = cartItemModelList.get(position).getOriginalProductPrice();
                String discountedPrice = cartItemModelList.get(position).getDiscountedProductPrice();
//                Long offerApplied = cartItemModelList.get(position).getOffersApplied();
                Boolean inStock = cartItemModelList.get(position).isInStock();
                ((cartItemViewHolder) holder).setCartItems(productId, resources, name, freeCoupon, originalPrice, discountedPrice, position, inStock);
                break;
            case CartItemModel.BALANCE_DETAILS_VIEW:
                int totalQuantity = 0;
                int totalPrice = 0;
                String deliveryCharge;
                int totalAmount = 0;

                int totalSavedAmount = 0;
                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM_VIEW && cartItemModelList.get(x).isInStock()) {
                        totalQuantity++;
                        totalPrice = totalPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedProductPrice());
                        totalSavedAmount = totalSavedAmount + (Integer.parseInt(cartItemModelList.get(x).getOriginalProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedProductPrice()));
                    }
                }
                if (totalPrice > 500) {
                    deliveryCharge = "FREE";
                    totalAmount = totalPrice;
                } else {
                    deliveryCharge = "40";
                    totalAmount = totalPrice + 40;
                }

                String productIds = cartItemModelList.get(position).getProductID();
                ((balanceDetailsViewHolder) holder).setCartBalanceDetails(totalPrice, totalQuantity, deliveryCharge, totalAmount, totalSavedAmount, productIds);
                break;
            default:
                return;

        }
        //////////fade in animation
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
        }
        //////////fade in animation

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public class cartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView cartProductImage;
        private TextView freeCouponNumber;
        private TextView freeCouponsApplied;
        private TextView offersApplied;
        private TextView productQuantity;
        private TextView cartProductName;
        private TextView originalProductPrice;
        private TextView discountedProductPrice;
        private ImageView freeCouponIcon;
        private LinearLayout removeFromCart;
        private LinearLayout saveForLater;
        private LinearLayout removeLinearLayout;
        private LinearLayout redeemCouponLayout;

        public cartItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            cartProductImage = itemView.findViewById(R.id.cart_product_image_iv);
            freeCouponNumber = itemView.findViewById(R.id.cart_free_coupons_available_tv);
            freeCouponsApplied = itemView.findViewById(R.id.cart_coupons_applied_tv);
            offersApplied = itemView.findViewById(R.id.cart_offers_applied_tv);
            productQuantity = itemView.findViewById(R.id.cart_product_quantity_tv);
            cartProductName = itemView.findViewById(R.id.cart_product_name_tv);
            originalProductPrice = itemView.findViewById(R.id.cart_original_price_tv);
            discountedProductPrice = itemView.findViewById(R.id.cart_discounted_price_of_product_tv);
            freeCouponIcon = itemView.findViewById(R.id.cart_free_coupons_image_iv);
            removeFromCart = itemView.findViewById(R.id.cart_remove_product_from_cart_btn);
            saveForLater = itemView.findViewById(R.id.cart_save_later_product_from_cart_btn);
            removeLinearLayout = itemView.findViewById(R.id.my_cart_remove_ll);
            redeemCouponLayout = itemView.findViewById(R.id.product_coupon_container_linearLayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void setCartItems(String productId, String resources, String productName, long freeCouponNo, String originalPrice, String discountedPrice, final int position, boolean inStock) {
            Glide.with(itemView.getContext()).load(resources).apply(new RequestOptions().placeholder(R.drawable.grid_placeholder)).into(cartProductImage);
            cartProductName.setText(productName);

            if (inStock) {
                originalProductPrice.setText("Rs." + originalPrice + "/-");
                originalProductPrice.setTextColor(Color.parseColor("#000000"));
                discountedProductPrice.setText("Rs." + discountedPrice + "/-");
                redeemCouponLayout.setVisibility(View.VISIBLE);
                ////////set product quantity dialog visible if product is in stock
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantity = (EditText) quantityDialog.findViewById(R.id.quantity_product_et);
                        Button cancelBtn = (Button) quantityDialog.findViewById(R.id.quantity_cancel_btn);
                        Button addQty = (Button) quantityDialog.findViewById(R.id.quantity_add_btn);

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                        addQty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                productQuantity.setText("Qty: " + quantity.getText());
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });
                ///////// number of coupon available
                if (freeCouponNo > 0) {
                    freeCouponNumber.setVisibility(View.VISIBLE);
                    freeCouponIcon.setVisibility(View.VISIBLE);
                    if (freeCouponNo == 1) {
                        freeCouponNumber.setText(freeCouponNo + " coupon available");
                    } else {
                        freeCouponNumber.setText(+freeCouponNo + " coupons available");
                    }
                } else {
                    freeCouponNumber.setVisibility(View.INVISIBLE);
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                }
                ////////number of offer applied
                //            if (offerApplied > 0) {
//                offersApplied.setVisibility(View.VISIBLE);
//                if (offerApplied == 1) {
//                    offersApplied.setText(offerApplied + " offer applied");
//                } else {
//                    offersApplied.setText(offerApplied + " offers applied");
//
//                }
//            } else {
//                offersApplied.setVisibility(View.INVISIBLE);
//            }
                offersApplied.setVisibility(View.GONE);
                freeCouponsApplied.setVisibility(View.GONE);
            } else {
                discountedProductPrice.setText("Out of stock");
                discountedProductPrice.setTextColor(itemView.getResources().getColor(R.color.errorColor));
                originalProductPrice.setText("");
                redeemCouponLayout.setVisibility(View.GONE);
                ////////set product quantity dialog invisible if product is out of stock
//                productQuantity.setText("Qty: "+0);
//                productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
//                productQuantity.setTextColor(Color.parseColor("#70000000"));
//                productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
                productQuantity.setVisibility(View.GONE);
                freeCouponNumber.setVisibility(View.GONE);
                freeCouponIcon.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCouponsApplied.setVisibility(View.GONE);


            }


            removeFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cartlist_query) {
                        ProductDetailsActivity.running_cartlist_query = true;
                        FirebaseQueries.removeCartItem(position, itemView.getContext() , totalAmountInCartFragment);
                        Toast.makeText(itemView.getContext(), "running from cart adapter", Toast.LENGTH_LONG).show();
                    }
                }
            });

            saveForLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Product saved", Toast.LENGTH_LONG).show();
                }
            });


            if (removeLayout) {
                removeLinearLayout.setVisibility(View.VISIBLE);
            } else {
                removeLinearLayout.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", cartItemModelList.get(position).getProductID());
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }

    public class balanceDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView totalItemQuantity;
        private TextView singleProductPrice;
        private TextView deliveryCharge;
        private TextView amountSaved;
        private TextView totalAmount;
        private TextView prideDetails;

        public balanceDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItemQuantity = itemView.findViewById(R.id.cart_balance_quantity_tv);
            singleProductPrice = itemView.findViewById(R.id.cart_balance_price_of_single_itme_tv);
            deliveryCharge = itemView.findViewById(R.id.cart_balance_delivery_charge_tv);
            amountSaved = itemView.findViewById(R.id.cart_balance_you_will_save_how_much_tv);
            totalAmount = itemView.findViewById(R.id.cart_balance_total_amount_tv);
            prideDetails = itemView.findViewById(R.id.price_details_tv);
        }

        public void setCartBalanceDetails(int totalPrice, int totalQuantity, String deliveryAmount, int total, int totalSavedAmount, final String productId) {
            if (totalQuantity > 1) {

                prideDetails.setText("Price Details (" + totalQuantity + " items)");
            } else {
                prideDetails.setText("Price Details (" + totalQuantity + "item)");
            }
            singleProductPrice.setText("Rs." + totalPrice + "/-");
            totalItemQuantity.setText(String.valueOf(totalQuantity));

            if (deliveryAmount.equals("FREE")) {
                deliveryCharge.setText(deliveryAmount);
            } else {
                deliveryCharge.setText("Rs." + deliveryAmount + "/-");
            }
            totalAmount.setText("Rs." + total + "/-");

            amountSaved.setText("You will save Rs. " + totalSavedAmount + "/- on this order");

            //// set total amount in cart fragment and order summery activity
            totalAmountInCartFragment.setText("Rs." + total + "/-");

            LinearLayout parent = (LinearLayout) totalAmountInCartFragment.getParent().getParent();
            if (totalPrice == 0) {
                FirebaseQueries.cartItemModelList.remove(FirebaseQueries.cartItemModelList.size() - 1);
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }

        }
    }
}
