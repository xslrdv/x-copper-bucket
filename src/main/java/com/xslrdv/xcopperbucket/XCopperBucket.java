/*
 * X Copper Bucket - A vanilla-friendly and well-balanced Copper Bucket mod that does not undermine the importance of the vanilla Iron Bucket.
 * Copyright (C) 2026 xslrdv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.xslrdv.xcopperbucket;

import com.xslrdv.xcopperbucket.registry.ModCapabilities;
import com.xslrdv.xcopperbucket.registry.ModCreativeTabs;
import com.xslrdv.xcopperbucket.registry.ModItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(XCopperBucket.MODID)
public class XCopperBucket {
    public static final String MODID = "xcopperbucket";

    public XCopperBucket(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModCapabilities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }
}
