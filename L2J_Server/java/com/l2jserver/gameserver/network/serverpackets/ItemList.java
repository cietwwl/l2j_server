/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.network.serverpackets;

import javolution.util.FastList;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public final class ItemList extends L2GameServerPacket
{
	private final PcInventory _inventory;
	private final L2ItemInstance[] _items;
	private final boolean _showWindow;
	private int length;
	private final FastList<L2ItemInstance> questItems;
	
	public ItemList(L2PcInstance cha, boolean showWindow)
	{
		_inventory = cha.getInventory();
		_items = cha.getInventory().getItems();
		_showWindow = showWindow;
		questItems = FastList.newInstance();
		for (int i = 0; i < _items.length; i++)
		{
			if (_items[i] != null && _items[i].isQuestItem())
			{
				questItems.add(_items[i]); // add to questinv
				_items[i] = null; // remove from list
			}
			else
				length++; // increase size
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x11);
		writeH(_showWindow ? 0x01 : 0x00);
		
		writeH(length);
		
		for (L2ItemInstance temp : _items)
		{
			if (temp == null || temp.getItem() == null)
				continue;
			
			writeD(temp.getObjectId());
			writeD(temp.getDisplayId());
			writeD(temp.getLocationSlot());
			writeQ(temp.getCount());
			writeH(temp.getItem().getType2()); // item type2
			writeH(temp.getCustomType1()); // item type3
			writeH(temp.isEquipped() ? 0x01 : 0x00);
			writeD(temp.getItem().getBodyPart());
			writeH(temp.getEnchantLevel()); // enchant level
			// race tickets
			writeH(temp.getCustomType2()); // item type3
			if (temp.isAugmented())
				writeD(temp.getAugmentation().getAugmentationId());
			else
				writeD(0x00);
			writeD(temp.getMana());
			writeD(temp.isTimeLimitedItem() ? (int) (temp.getRemainingTime() / 1000) : -9999);
			writeH(temp.getAttackElementType());
			writeH(temp.getAttackElementPower());
			for (byte i = 0; i < 6; i++)
			{
				writeH(temp.getElementDefAttr(i));
			}
			// Enchant Effects
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
		}
		if (_inventory.hasInventoryBlock())
		{
			writeH(_inventory.getBlockItems().length);
			writeC(_inventory.getBlockMode());
			for (int i : _inventory.getBlockItems())
			{
				writeD(i);
			}
		}
		else
		{
			writeH(0x00);
		}
	}
	
	@Override
	public void runImpl()
	{
		getClient().sendPacket(new ExQuestItemList(questItems, getClient().getActiveChar().getInventory()));
	}
}