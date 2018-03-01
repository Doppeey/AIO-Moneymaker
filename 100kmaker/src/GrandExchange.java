import java.util.Random;

import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

public class GrandExchange
{
  private final Script parent;
  private final org.osbot.rs07.api.map.Area GRAND_EXCHANGE_CENTER = new org.osbot.rs07.api.map.Area(3154, 3479, 3174, 3500);
  private final int[] collectWidget = { 465, 6, 1 };
  private final int[] buyOfferWidget = { 465, 7, 26 };
  private final int[] itemSelectWidget = { 465, 24, 21 };
  private final int[] preSlectionWidget = { 162, 38, 0 };
  private final int[] preNumberWidget = { 162, 32 };
  private final int[] searchIndexWidget = { 162, 38 };
  private final int[] searchTextWidget = { 162, 33 };
  private final int[] chatboxWidget = { 162, 42 };
  private final int[] priceWidget = { 465, 24, 39 };
  private final int[] amountWidget = { 465, 24, 32 };
  private final int[] setPriceWidget = { 465, 24, 12 };
  private final int[] setAmountWidget = { 465, 24, 7 };
  private final int[] confirmWidget = { 465, 24, 54 };
  private final int[] setAllWidget = { 465, 24, 6 };

  public GrandExchange(Script script) {
    parent = script;
  }

  public void openGE() {
    RS2Object geBooth = (RS2Object)parent.getObjects().closest(new String[] { "Grand Exchange booth" });
    NPC exchangeWorker = (NPC)parent.getNpcs().closest(new String[] { "Grand Exchange Clerk" });

    int random = new Random().nextInt(10);
    if ((geBooth != null) && (random < 5)) {
      geBooth.interact(new String[] { "Exchange" });
      new ConditionalSleep(2500, 3000)
      {
        public boolean condition() {
          return parent.getGrandExchange().isOpen();
        }
      }.sleep();
    }

    if ((exchangeWorker != null) && (random >= 5)) {
      exchangeWorker.interact(new String[] { "Exchange" });
      new ConditionalSleep(2500, 3000)
      {
        public boolean condition()
        {
          return parent.getGrandExchange().isOpen();
        }
      }.sleep();
    }
  }


  public void collectItems(boolean bank)
  {
    if ((parent.getGrandExchange().isOpen()) && (collectButton() != null)) {
      if (bank) {
        collectButton().interact(new String[] { "Collect to bank" });
      } else {
        collectButton().interact(new String[] { "Collect to inventory" });
      }





      new ConditionalSleep(2500, 3000)
      {
        public boolean condition()
        {
          return GrandExchange.this.collectButton() != null;
        }
      }.sleep();
    }
  }




  private void initBuyOffer(String itemName)
  {
    if ((parent.getGrandExchange().isOpen()) && (!parent.getGrandExchange().isBuyOfferOpen()) &&
      (buyOfferSlotOne() != null)) {
      buyOfferSlotOne().interact(new String[] { "Create buy offer" });
      new ConditionalSleep(2500, 3000)
      {
        public boolean condition() {
          return parent.getGrandExchange().isBuyOfferOpen();
        }
      }.sleep();
    }
  }


    public void createSellOffer(String itemName, int price, int amount)
  {
    if (parent.getGrandExchange().isOpen()) {
      if (!parent.getGrandExchange().isSellOfferOpen()) {
        initSellOffer(itemName);
      }
      setPrice(price);
      setAmount(amount);
      if ((confirmButton() != null) && (getItem().equals(itemName)) && (getPrice() == price) && (getAmount() == amount)) {
        confirmButton().interact(new String[] { "Confirm" });
        new ConditionalSleep(2500, 3000)
        {
          public boolean condition() {
            return !parent.getGrandExchange().isSellOfferOpen();
          }
        }.sleep();
      }
    }
  }


  private void initSellOffer(String itemName)
  {
    if ((parent.getGrandExchange().isOpen()) && (!parent.getGrandExchange().isSellOfferOpen())) {
      if (parent.getInventory().contains(new String[] { itemName })) {
        org.osbot.rs07.api.model.Item sellItem = parent.getInventory().getItem(new String[] { itemName });
        sellItem.interact(new String[] { "Offer" });
      }





      new ConditionalSleep(2500, 3000)
      {
        public boolean condition()
        {
          return parent.getGrandExchange().isSellOfferOpen();
        }
      }.sleep();
    }
  }

  private void setPrice(int itemPrice)
  {
    if ((parent.getGrandExchange().isOpen()) && (parent.getGrandExchange().isOfferScreenOpen())) {
      if ((priceText().getMessage() != null) &&
        (Integer.parseInt(priceText().getMessage().replaceAll("[\\D]", "")) != itemPrice) &&
        (preNumber() != null) && (!preNumber().isVisible())) {
        if ((itemPrice != 0) && (priceButton() != null) && (preNumber() != null) && (!preNumber().isVisible())) {
          priceButton().interact(new String[] { "Enter price" });
          new ConditionalSleep(2500, 3000)
          {
            public boolean condition() {
              return GrandExchange.this.preNumber().isVisible();
            }
          }.sleep();
        }

        if ((itemPrice == 0) && (allButton() != null)) {
          allButton().interact(new String[] { "All" });
          new ConditionalSleep(2500, 3000)
          {
            public boolean condition() {
              return GrandExchange.this.preNumber().isVisible();
            }
          }.sleep();
        }
      }


      if ((getPrice() != itemPrice) && (preNumber() != null) && (preNumber().isVisible()) && (searchText() != null) &&
        (!searchText().getMessage().replaceAll("[\\D]", "").equals(String.valueOf(itemPrice))) &&
        (!chatboxText().isVisible())) {
        parent.getKeyboard().typeString(String.valueOf(itemPrice), true);
      }
    }
  }

  private void setAmount(int itemAmount)
  {
    if ((parent.getGrandExchange().isOpen()) && (parent.getGrandExchange().isOfferScreenOpen())) {
      if ((amountText().getMessage() != null) &&
        (Integer.parseInt(amountText().getMessage().replaceAll("[\\D]", "")) != itemAmount) &&
        (amountButton() != null) && (preNumber() != null) && (!preNumber().isVisible())) {
        amountButton().interact(new String[] { "Enter quantity" });
        new ConditionalSleep(2500, 3000)
        {
          public boolean condition() {
            return GrandExchange.this.preNumber().isVisible();
          }
        }.sleep();
      }




      if ((getAmount() != itemAmount) && (preNumber() != null) && (preNumber().isVisible()) && (searchText() != null) &&
        (!searchText().getMessage().replaceAll("[\\D]", "").equals(String.valueOf(itemAmount))) &&
        (!chatboxText().isVisible())) {
        parent.getKeyboard().typeString(String.valueOf(itemAmount), true);
      }
    }
  }

    private int getPrice() {
    if ((priceText() != null) && (priceText().getMessage() != null)) {
      return Integer.parseInt(priceText().getMessage().replaceAll("[\\D]", ""));
    }
    return -1;
  }

  private int getAmount() {
    if ((amountText() != null) && (amountText().getMessage() != null)) {
      return Integer.parseInt(amountText().getMessage().replaceAll("[\\D]", ""));
    }
    return -1;
  }

  private String getItem() {
    if (itemSelection() != null) {
      return getName(itemSelection().getItemId());
    }
    return "Invalid";
  }

    private RS2Widget buyOfferSlotOne() {
    RS2Widget widget = parent.getWidgets().get(buyOfferWidget[0], buyOfferWidget[1], buyOfferWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget confirmButton() {
    RS2Widget widget = parent.getWidgets().get(confirmWidget[0], confirmWidget[1], confirmWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget collectButton() {
    RS2Widget widget = parent.getWidgets().get(collectWidget[0], collectWidget[1], collectWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

    private RS2Widget itemSelection() {
    RS2Widget widget = parent.getWidgets().get(itemSelectWidget[0], itemSelectWidget[1], itemSelectWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget priceButton() {
    RS2Widget widget = parent.getWidgets().get(setPriceWidget[0], setPriceWidget[1], setPriceWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget priceText() {
    RS2Widget widget = parent.getWidgets().get(priceWidget[0], priceWidget[1], priceWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget chatboxText() {
    RS2Widget widget = parent.getWidgets().get(chatboxWidget[0], chatboxWidget[1]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget preNumber() {
    RS2Widget widget = parent.getWidgets().get(preNumberWidget[0], preNumberWidget[1]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget searchText() {
    RS2Widget widget = parent.getWidgets().get(searchTextWidget[0], searchTextWidget[1]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget amountButton() {
    RS2Widget widget = parent.getWidgets().get(setAmountWidget[0], setAmountWidget[1], setAmountWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private RS2Widget amountText() {
    RS2Widget widget = parent.getWidgets().get(amountWidget[0], amountWidget[1], amountWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }

  private String getName(int id) {
    ItemDefinition itemDef = ItemDefinition.forId(id);
    if ((itemDef != null) && (itemDef.getName() != null)) {
      return itemDef.getName();
    }
    return null;
  }

  private RS2Widget allButton() {
    RS2Widget widget = parent.getWidgets().get(setAllWidget[0], setAllWidget[1], setAllWidget[2]);
    if (widget != null) {
      return widget;
    }
    return null;
  }
}
