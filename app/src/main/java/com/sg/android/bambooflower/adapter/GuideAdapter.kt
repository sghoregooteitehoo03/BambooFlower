package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.GuideViewHolder
import com.sg.android.bambooflower.data.Guide
import com.sg.android.bambooflower.databinding.ItemGuideBinding

class GuideAdapter : RecyclerView.Adapter<GuideViewHolder>() {
    private val guideList = listOf<Guide>(
        Guide(
            "퀘스트",
            "퀘스트란 대나무꽃에서 제공하는 미션들을 의미합니다. 매일 총 2개의 퀘스트를 수행할 수 있으며, 수행한 모습을 유저들과 공유하여 인정을 받아 퀘스트를 완료하시면 됩니다. 퀘스트를 수행하는 방법은 퀘스트마다 방법들이 적혀있으며 방법과 이미지를 참고하여 퀘스트를 수행하시면 됩니다. 퀘스트를 완료하게 되면 보상을 받으실 수 있습니다. 보상을 통해 경험치와 포인트를 획득해 보세요. 수락한 퀘스트는 매일 초기화되니 유의해주시길 바랍니다."
        ),
        Guide(
            "하루일기",
            "하루일기란 하루를 마무리하면서 오늘 있었던 일이나 느꼈던 생각들을 정리하는 일기를 말합니다. 유저들에게 공개되지 않고 자신만 볼 수 있으니 안심하고 자유롭게 작성해보시길 바랍니다. 하루일기의 내용은 서버에 저장되지 않습니다. 그러니 앱을 삭제하거나 데이터를 초기화할 시 작성하셨던 내용들이 모두 지워질 수 있으니 유의해주시길 바랍니다."
        ),
        Guide(
            "정원",
            "정원이란 지금까지 성장시킨 꽃이나 상점에서 구매한 아이템들을 이용하여 땅을 꾸밀 수 있는 곳을 의미합니다. 가지고 있는 아이템을 꾹 눌러 원하는 땅 위치에 아이템을 배치하여 정원을 자유롭게 꾸밀 수 있으며 꾸민 정원을 저장하고 싶을 때는 오른쪽 위에 있는 저장 버튼을 눌러 저장하실 수 있습니다. 정원 데이터는 아직 서버에 저장하지 않고 있습니다. 그러니 앱을 삭제하거나 데이터를 초기화할 시 데이터가 모두 지워질 수 있으니 유의해주시길 바랍니다."
        ),
        Guide(
            "경험치",
            "경험치란 꽃의 성장률을 의미합니다. 퀘스트 보상이나 하루일기를 통해 얻으실 수 있으며, 경험치를 통해 꽃을 성장시킬 수 있습니다. 경험치가 100%가 되면 꽃이 모두 성장하게 됩니다. 성장한 꽃은 인벤토리에 추가되며 정원을 꾸밀 수 있는 아이템으로 사용할 수 있습니다."
        ),
        Guide(
            "포인트",
            "포인트란 상점에서 이용하는 돈을 의미합니다. 퀘스트 보상이나 하루일기를 통해 포인트를 얻으실 수 있으며 상점에서 아이템을 구매할 때 사용하실 수 있습니다."
        )
    )
    lateinit var itemListener: GuideItemListener

    interface GuideItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view =
            ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(guideList[position])
    }

    override fun getItemCount() =
        guideList.size

    fun setOnItemListener(_itemListener: GuideItemListener) {
        itemListener = _itemListener
    }

    fun expandItem(pos: Int) {
        guideList[pos].isExpand = !guideList[pos].isExpand
        notifyItemChanged(pos)
    }
}