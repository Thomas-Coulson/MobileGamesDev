package com.example.mobilegamedev_a0050289;

import android.graphics.Rect;

enum NodeType
{
    wall,
    path,
    coin,
    none;
}

public class GridNode
{
    private NodeType m_nodeType = NodeType.none;
    private int m_size;
    private int m_xPos = 0, m_yPos = 0;
    private Rect m_hitBox;

    public GridNode(NodeType nodeType, int size, int xPos, int yPos)
    {
        m_nodeType = nodeType;
        m_size = size;
        m_xPos = xPos;
        m_yPos = yPos;

        m_hitBox = new Rect(m_xPos, m_yPos, m_xPos + m_size, m_yPos + m_size);
    }

    public NodeType GetNodeType()
    {
        return m_nodeType;
    }

    public Rect GetHitBox()
    {
        return m_hitBox;
    }
}
