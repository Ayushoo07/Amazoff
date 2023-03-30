package com.driver;

import org.springframework.stereotype.Repository;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;

@Repository
public class OrderRepository
{
    HashMap<String,Order> orderDb=new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDb=new HashMap<>();
    HashMap<String,List<String>> partnerOrderDb=new HashMap<>();


    public void addOrder(Order order)
    {
        String key=order.getId();
        orderDb.put(key, order);
    }

    public void  addPartner(String partnerId)
    {
        DeliveryPartner obj=new DeliveryPartner(partnerId);
        partnerDb.put(partnerId,obj);
    }


    public void  addOrderPartnerPair(String orderId,String partnerId)
    {
        DeliveryPartner obj=partnerDb.get(partnerId);
        obj.setNumberOfOrders(obj.getNumberOfOrders()+1);
        partnerDb.put(partnerId,obj);



        if (partnerOrderDb.containsKey(partnerId))
        {
            List<String> orders=partnerOrderDb.get(partnerId);
            orders.add(orderId);
            partnerOrderDb.put(partnerId, orders);
        }
        else
        {

            List<String> orders=new ArrayList<>();
            orders.add(orderId);
            partnerOrderDb.put(partnerId, orders);
        }
    }

    public Order getOrderById(String orderId) {

        return orderDb.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId)
    {
        return partnerDb.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId)
    {
        return partnerDb.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId)
    {
        return partnerOrderDb.get(partnerId);
    }

    public List<String> getAllOrders()
    {
        List<String> orders=new ArrayList<>();

        for (Order order: orderDb.values())
        {
            orders.add(order.getId());
        }

        return orders;
    }

    public int getCountOfUnassignedOrders()
    {
        List<String> allOrders=getAllOrders();



        int assignedCnt=0;

        for (List<String> ls : partnerOrderDb.values())
        {
            assignedCnt+=ls.size();
        }

        return allOrders.size()-assignedCnt;

    }

    public int  getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId)
    {
        String[] arr=time.split(":");
        int currTime=Integer.parseInt(arr[0])*60 + Integer.parseInt(arr[1]);

        List<String> orders=partnerOrderDb.get(partnerId);
        int cnt=0;

        for (String order:orders)
        {
            if(orderDb.get(order).getDeliveryTime()>currTime)
            {
                cnt++;
            }
        }

        return cnt;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId)
    {
        List<String> orders=partnerOrderDb.get(partnerId);

        int maxTime=0;
        for (String order: orders) {
            int deliveryTime=orderDb.get(order).getDeliveryTime();

            if(deliveryTime>maxTime)
                maxTime=deliveryTime;
        }

        int hh=maxTime/60;
        int mm=maxTime%60;

        String time="";
        if(hh/10==0)
        {
            time="0"+String.valueOf(hh)+":";
        }
        else
        {
            time=String.valueOf(hh)+":";
        }

        if(mm/10==0)
        {
            time+="0"+String.valueOf(mm);
        }
        else
        {
            time+=String.valueOf(mm);
        }

        return time;
    }

    public void deletePartnerById(String partnerId)
    {
        partnerOrderDb.remove(partnerId);

        partnerDb.remove(partnerId);
    }

    public void deleteOrderById( String orderId)
    {
        orderDb.remove(orderId);

        for(Map.Entry<String,List<String>> hash : partnerOrderDb.entrySet())
        {
            if (hash.getValue().contains(orderId))
            {
                List<String> orders=hash.getValue();
                orders.remove(orderId);
                partnerOrderDb.put(hash.getKey(),orders);

                DeliveryPartner obj=partnerDb.get(hash.getKey());
                obj.setNumberOfOrders(obj.getNumberOfOrders()-1);

                partnerDb.put(hash.getKey(), obj);

                break;
            }
        }

    }



}
