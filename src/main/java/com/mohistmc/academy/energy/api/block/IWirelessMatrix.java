package com.mohistmc.academy.energy.api.block;

/**
 * 无线矩阵接口 —— 整个无线网络的"核心"。
 * 每个网络只能有一个矩阵，矩阵定义了网络的基础参数。
 *
 * @author WeAthFolD (original), Mgazul (port)
 */
public interface IWirelessMatrix extends IWirelessTile {

    /**
     * @return 矩阵可容纳的节点数量
     */
    int getCapacity();

    /**
     * @return 每 tick 允许在节点间平衡传输的最大能量
     */
    double getBandwidth();

    /**
     * @return 矩阵信号的最大可达范围
     */
    double getRange();
}
